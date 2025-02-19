<a href="https://hits.seeyoufarm.com"><img src="https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2F혁진98%2F프로젝트&count_bg=%2379C83D&title_bg=%23555555&icon=react.svg&icon_color=%23E7E7E7&title=hits&edge_flat=false"/></a>

<div align= "center">
    <img src="https://capsule-render.vercel.app/api?type=rect&color=0:da5d5d,100:dfe246&height=240&text=Connectify&animation=twinkling&fontColor=1a1a1a&fontSize=40" />
    </div>
    <div style="text-align: left;"> 
    <h2 style="border-bottom: 1px solid #d8dee4; color: #282d33;">  </h2>  
    <div style="font-weight: 700; font-size: 15px; text-align: left; color: #282d33;">  </div> 
    </div>
    <div style="text-align: left;">
    <h2 style="border-bottom: 1px solid #d8dee4; color: #282d33;"> 🛠️ Tech Stacks </h2> <br> 
   개발 도구: IntelliJ IDEA <br>
   프로그래밍 언어: Java, HTML, CSS, JavaScript<br>
   프레임워크 및 라이브러리: Spring Boot, Thymeleaf, JPA, Hibernate<br>
   데이터베이스: MySQL<br>
   ETC: Spring Security<br>
 <h2 style="border-bottom: 1px solid #d8dee4; color: #282d33;">  </h2>  
  <h2 style="border-bottom: 1px solid #d8dee4; color: #282d33;"> 제작 </h2> <br>
  <h4>개발자 : 이혁진</h4>
  <h4>제작기간 : 2024.11.01~2024.12.01</h4>
  <h4>개요 :  커뮤니티 웹사이트 개발 프로젝트로, 회원가입/로그인, 게시판, 댓글 기능, 쪽지 발송, 파일 업로드 등 커뮤니티 운영에 필요한 핵심 기능을 구현. Spring Boot 기반의 Thymeleaf를 사용해 웹 화면을 개발.</h4>


<h1>ERD</h1>


![erd](https://github.com/user-attachments/assets/ec712ceb-fdff-4150-9eab-2317ed5db322)


<h1>구현 페이지</h1>

![구현](https://github.com/user-attachments/assets/91c70ddb-5ce2-46b6-8c6f-3cd5523bf179)


<h1>기능 요약</h1>
<ul>
  <li>
    <strong>게시판:</strong>
    <p>사용자는 게시글을 작성하고 수정하거나 삭제할 수 있습니다.</p>
  </li>
  <li>
    <strong>공지사항:</strong>
    <p>관리자가 중요한 공지사항을 게시할 수 있습니다.</p>
  </li>
  <li>
    <strong>회원관리:</strong>
    <p>사용자는 자신의 계정에 로그인하고 로그아웃할 수 있으며, 비밀번호나 유저네임을 변경할 수 있습니다.</p>
  </li>
  <li>
    <strong>댓글:</strong>
    <p>사용자는 게시글에 댓글을 작성하고, 다른 사용자의 댓글에 대댓글을 달 수 있으며, 작성한 댓글을 삭제할 수 있습니다.</p>
  </li>
  <li>
    <strong>검색:</strong>
    <p>게시물에 대한 검색 기능을 통해 사용자가 원하는 정보를 쉽고 빠르게 찾을 수 있도록 돕습니다.</p>
  </li>
  <li>
    <strong>친구:</strong>
    <p>사용자는 다른 사용자를 친구로 추가하고, 친구 목록을 관리할 수 있습니다. 이를 통해 친목을 도모할 수 있습니다.</p>
  </li>
  <li>
    <strong>쪽지:</strong>
    <p>사용자는 다른 사용자와 쪽지를 통해 사적인 소통을 할 수 있습니다. 쪽지 발송 및 수신, 삭제 등의 기능을 지원합니다.</p>
  </li>
  <li>
    <strong>페이징:</strong>
    <p>게시글이나 쪽지가 많을 경우 페이지가 나뉘어져 있어 사용자가 편리하게 다른 페이지를 탐색할 수 있습니다.</p>
  </li>
  <li>
    <strong>파일 업로드:</strong>
    <p>사용자는 게시글 작성 시 이미지나 기타 파일을 첨부할 수 있으며, 게시글에서 이를 미리 보기하거나 다운로드할 수 있습니다.</p>
  </li>
</ul>

<h1>트러블 슈팅</h1>
<ul>
    <li>
        <strong>데이터가 많아질수록 속도가 느려짐</strong><br>
        처음에는 <code>findAll()</code>로 전체 게시글을 불러왔는데, 게시글이 많아지면서 페이지 로딩이 느려짐<br>
        <strong>해결법:</strong> 페이징 처리 적용하기 (<code>JPA Pageable</code> 활용)
    </li>
    <li>
        <strong>UI/UX가 생각처럼 나오지 않음</strong><br>
        Figma 같은 디자인 툴 없이 바로 개발을 시작하여 네이버 카페 같은 디자인을 만들고 싶었지만 이상하게 나옴<br>
        <strong>해결법:</strong> 개발자 도구 (<code>F12</code>)를 활용하여 네이버 카페의 코드를 참고하면서 직접 수정하기
    </li>
</ul>


