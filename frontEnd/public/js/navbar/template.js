const getNavBarTemplate = () => `
    <div class="navbar__logo"><a href="/">Oil Pocket<a/></div>
    <div class="navbar__btns">
        <span><a href="/userDetail">jaewon님 안녕하세요</a></span>
        <span> | </span>
        <span class="navbar__myPageBtn"><a href="/login" data-link>마이 페이지</a></span>
        <span> | </span>
        <span>한국</span>
        <span> / </span>
        <span>영어</span>
        <span> | </span>
        <span>다크모드</span>
    </div>
`;

export { getNavBarTemplate }