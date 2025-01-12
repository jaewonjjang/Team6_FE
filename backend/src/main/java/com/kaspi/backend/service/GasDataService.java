package com.kaspi.backend.service;

import com.kaspi.backend.dao.GasDetailDao;
import com.kaspi.backend.dao.GasStationDao;
import com.kaspi.backend.domain.GasDetail;
import com.kaspi.backend.domain.GasDetailDto;
import com.kaspi.backend.domain.GasStation;
import com.kaspi.backend.domain.GasStationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GasDataService {
    private final GasStationDao gasStationDao;
    private final GasDetailDao gasDetailDao;
    private final Map<String, GasStation> gasStationInfos = new HashMap<>();
    private final Map<String, GasStationDto> cacheMap = new ConcurrentHashMap<>();

    @Transactional
    public void insertGasInfo(final String fileName, GasDetailCallback callback) throws IOException, InterruptedException {
        try {
            Thread.sleep(4000);
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "euc-kr"));
            String line = br.readLine();
            LocalDate date = LocalDate.now();
            while ((line = br.readLine()) != null) {
                String[] attribute = line.split(",");
                if (attribute.length <= 1) break;
                attribute[3] = makeEndAddress(attribute[3]);
                String key = makeCacheKey(attribute);
                System.out.println(key);

                //가스 도로명+브랜드 겹치는거 있는지 체크
                if (!gasStationInfos.containsKey(key)) {
                    GasStation gasStation = GasStation.parseGasStation(attribute);
                    //디비에 이미 주유소 정보가 있다면 넣지 않음
                    Optional<GasStation> optionalGasStation = gasStationDao.findByAddressAndBrand(gasStation.getAddress(), gasStation.getBrand());
                    saveGasStationIfNotExists(key, gasStation, optionalGasStation);
                }
                GasStation gasStation = gasStationInfos.get(key);

                List<GasDetail> gasDetails = callback.makeGasDetailAndSaveToDB(gasStation, gasDetailDao, attribute, date);
                if (cacheMap.containsKey(key)) {
                    GasStationDto gasStationDto = cacheMap.get(key);
                    gasStationDto.addGasDetailList(GasDetailDto.newDtoList(gasDetails));
                    continue;
                }
                cacheMap.put(key, GasStationDto.newInstance(gasStationInfos.get(key), GasDetailDto.newDtoList(gasDetails)));
            }
            file.delete();
        } catch (FileNotFoundException e) {
            insertGasInfo(fileName, callback);
        }

    }

    private void saveGasStationIfNotExists(String key, GasStation gasStation, Optional<GasStation> optionalGasStation) {
        if (optionalGasStation.isEmpty()) {
            gasStationDao.save(gasStation);
            gasStationInfos.put(key, gasStation);
            return;
        }
        gasStationInfos.put(key, optionalGasStation.get());
    }

    public String makeCacheKey(String[] attribute) {
        return makeEndAddress(attribute[3]) + ":" + attribute[4];
    }

    private String makeEndAddress(String address) {
        String[] temp = address.split(" |\\(");
        int idx = 0;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].matches(".*로$") || temp[i].matches(".*길$")) {
                idx = i;
                break;
            }
        }
        String result = "";
        for (int i = idx; i < temp.length; i++) {
            if (temp[i].contains(")")) {
                break;
            }
            result += temp[i] + " ";
        }
        return result.trim();
    }

    public void initCache() {
        cacheMap.clear();
    }
}
