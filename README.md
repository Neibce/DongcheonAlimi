# 동천 알리미 ![CI](https://github.com/Neibce/Dongcheon-Alimi/actions/workflows/android.yml/badge.svg) [![CodeFactor](https://www.codefactor.io/repository/github/neibce/dongcheonalimi/badge)](https://www.codefactor.io/repository/github/neibce/dongcheonalimi)
- 2020년 2분기
- [API Server Repository](https://github.com/Neibce/Dongcheon-Alimi-API)
## 제작 동기
집에서 학교의 시간표, 급식, 학사일정 등을 알고 싶을 때에는 나이스 로그인 등 항상 복잡한 과정을 거쳐야만 원하는 정보를 얻을 수 있었습니다. 친구들 또한 이러한 불편함을 느끼고 있다는 것을 알게 되었고, 이러한 불편함을 해결해보고자 직접 안드로이드 어플리케이션 제작에 나서게 되었습니다.
## 어플리케이션 구현
어플리케이션은 Android Studio로 Java를 사용하여 개발하게 되었습니다. Google의 Material Design에서 제공하는 오픈소스를 활용하고 가이드라인에 최대한 맞춰 UI/UX 디자인을 진행하였고, Activity에 Fragment들과 BottomNavigaionBar를 이용하여 탭 간 이동 방식으로 어플의 전체적인 화면을 구성하였습니다.

어플리케이션과 API 서버 간의 통신을 위해서 Thread, AsyncTask, HttpURLConnection을 사용하여 통신하였습니다. 또한, 주위 친구들이 모바일 데이터 제한 등의 이유로 네트워크 연결이 필요한 어플 사용에 불편을 느끼는 경우가 많았습니다. 그래서 오프라인 환경에서도 대부분의 기능을 이용할 수 있도록 필요한 정보들을 서버에서 내려받아 SharedPreferences를 통하여 JSON 형식으로 기기에 저장하여 인터넷에 접속하지 않더라도 실시간 소통이 요구되는 게시판을 제외한 모든 기능을 이용할 수 있게 하였고, 서버의 부하 또한 줄일 수 있었습니다.

## 주요 기능 구현
### 시험 일정(D-DAY)
  
1년 간의 모든 시험 일정을 데이터베이스에 업로드 해두고, 매년 서버에서 사용자의 휴대폰로 다운받아 저장(SharedPreferences)하게 하였습니다. 시험까지 남은 일수를 어플의 첫 화면에 표시하여 어플을 켤 때마다 공부 의지가 들 수 있도록 하였습니다. (결과 사진 1번 상단)

### 급식 (식단표)
  
식단표는 사용자 스마트폰에서 매달 나이스 홈페이지의 식단표를 정규표현식을 통해 크롤링하여 JSON 형식으로 변환한 뒤 기기에 저장하였습니다. 어플의 첫 페이지에 ViewPager를 두어 중식, 석식을 넘겨가며 볼 수 있고, 1달 내의 급식을 모두 조회할 수 있도록 하였습니다. (결과 사진 1번 중단)

### 시간표
시간표는 전체 학년, 반의 시간표를 데이터베이스에 업로드 해두고, 사용자가 필요로 하는 학년, 반의 시간표를 다운받아 기기에 저장하고 TableLayout을 통해 띄워주었습니다. (결과 사진 1번 하단)

### 게시판(공지사항, 건의사항)
게시글 목록을 표시하기 위해 RecyclerView를 통해 스크롤의 위치에 따라 서버에서 게시글 목록을 가져와 추가적으로 로드하는 무한 스크롤 방식을 구현하였습니다. 게시글 작성 부분에서는 우리 학교 학생들만 글을 쓰게 하면서도 로그인, 회원가입 등의 불편한 과정은 생략하고 싶었습니다. 그래서 게시글 작성 시 ‘우리 학교 도서실은 몇 층에 있나요?’ 와 같은 우리 학교 학생들만 알 수 있는 질문들을 랜덤하게 서버에서 가져와 정답을 맞춘 경우에만 게시글이 작성되도록 하여 외부인이 함부로 게시글을 작성하지 못하도록 하였습니다.(결과 사진 5번) 또한, FCM Token을 통해 로그인/회원가입 없이도 각 유저(기기)를 구분할 수 있도록 하였습니다. FCM으로 새 글 푸시 알림 또한 구현하였습니다.(결과 사진 2,3번)

### 학사일정
학사일정은 매달 사용자 스마트폰에서 학교 홈페이지의 학사일정 API를 통해 JSON으로 받아오고, 필요한 정보만 남게 가공한 뒤 기기에 저장, RecyclerView를 통해 item들을 표시하였습니다. (결과 사진 5번)

### 설정
기본으로 제공되는 PreferenceScreen을 기반으로 제작하였으며, 친구들의 요청에 따라 어플의 테마색을 직접 설정할 수 있게 하였고, 각종 데이터들을 수동으로 새로 다운받는 등의 기능을 넣어 사용자의 입맛에 맞도록 제어할 수 있게 하였습니다. (결과 사진 6번)

## 실제 구현 결과
![Screenshot_20200927-230759_ ](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/8133c553-fbaa-4de5-823e-20be4c1b2c92)|![Screenshot_20200927-233445_ ](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/39d298e0-2922-4952-a5dc-6e97f813b815)|![Screenshot_20200907-230914_ ](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/8b94ff06-fdab-4a8a-98db-711a56a58b9d)
|---|---|---|
![Screenshot_20201004-175856_ ](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/9010867d-5c5c-468a-b039-52f3a41692eb)|![Screenshot_20200907-230934_ ](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/59227acf-a226-41ac-a73d-d863cde3c4bc)|![Screenshot_20200907-231019_ ](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/0ebe317a-ac9e-4d3f-a339-9e62479feb52)
![Screenshot_20201004-175641_Google Play Store](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/9f712c9d-84a2-4af9-b06e-8984460484f3)

## 결과 및 정리
어플을 완성한 뒤, 어플을 플레이스토어에 등록하고 교내 홍보를 통해 학생들에게 알리게 되었습니다. 친구들이 생각보다 더욱 유용하게 사용해줘서 굉장히 놀랐고 편리하다는 의견이 많았습니다. 특히 건의사항 게시판에 대해 평소에는 학교생활 중 불편한 점이 생겨도 건의할 방법이 없었는데, 이 어플을 통해 직접 건의사항을 올릴 수 있게 된 점에 대해 호평과 긍정적인 반응이 많았습니다. 또, 어플을 통해 올라온 건의 글의 내용들은 실제로 학생회에 반영되어, 실제 학교 환경이 개선되어 가는 것을 보면서 뿌듯함과 몇 달 간의 고생에 대해 보상을 받은 듯한 느낌이 들었습니다. 다음에 좀 더 공부해서 iOS용 앱도 제작해 앱스토어에 업로드 해보고 싶습니다.
