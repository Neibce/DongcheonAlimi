# 동천 알리미 ![CI](https://github.com/Neibce/Dongcheon-Alimi/actions/workflows/android.yml/badge.svg) [![CodeFactor](https://www.codefactor.io/repository/github/neibce/dongcheonalimi/badge)](https://www.codefactor.io/repository/github/neibce/dongcheonalimi)

[English](./README_EN.md)

학교 시간표, 급식, 학사일정을 한 곳에서 확인할 수 있는 안드로이드 앱

2020년 2분기 개발 / Google Play Store 배포

- [API Server Repository](https://github.com/Neibce/DongcheonAlimi-Server)

## 제작 동기

학교의 시간표, 급식, 학사일정 등을 확인하려면 매번 나이스에 로그인해야 하는 불편함이 있었습니다. 친구들도 같은 불편함을 느끼고 있어서, 이를 해결하고자 직접 안드로이드 앱을 개발하게 되었습니다.

## 기술적 구현

### 앱 아키텍처
- Android Studio, Java 기반 개발
- Material Design 가이드라인 준수
- Fragment + BottomNavigationBar 탭 구조

### 서버 통신
- Thread, AsyncTask, HttpURLConnection으로 API 통신

### 오프라인 지원
모바일 데이터가 제한된 친구들을 위해 **오프라인 모드**를 구현했습니다. 서버에서 받은 데이터를 SharedPreferences에 JSON으로 저장하여, 인터넷 없이도 게시판을 제외한 모든 기능을 사용할 수 있습니다. 서버 부하도 줄이는 효과가 있었습니다.

## 주요 기능

### 시험 D-Day
1년치 시험 일정을 DB에서 다운받아 기기에 저장. 메인 화면에 남은 일수를 표시하여 동기부여 효과.

### 급식
나이스 홈페이지에서 정규표현식으로 식단표 크롤링 → JSON 변환 → 기기 저장. ViewPager로 중식/석식 스와이프 조회.

### 시간표
학년/반별 시간표를 DB에서 다운받아 TableLayout으로 표시.

### 게시판 (공지사항/건의사항)
- RecyclerView + 무한 스크롤 구현
- **퀴즈 기반 인증**: 로그인 없이 외부인 차단을 위해, 게시글 작성 시 "우리 학교 도서실은 몇 층?" 같은 교내 학생만 아는 퀴즈를 랜덤 출제. 정답 시에만 작성 가능.
- FCM Token으로 유저 식별 및 새 글 푸시 알림 구현

### 학사일정
학교 홈페이지 API에서 JSON으로 받아와 RecyclerView로 표시.

### 설정
테마색 커스터마이징, 데이터 수동 갱신 기능.

## 스크린샷

| 메인 화면 | 공지사항 | 건의사항 |
|:---:|:---:|:---:|
| ![메인](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/8133c553-fbaa-4de5-823e-20be4c1b2c92) | ![공지사항](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/39d298e0-2922-4952-a5dc-6e97f813b815) | ![건의사항](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/8b94ff06-fdab-4a8a-98db-711a56a58b9d) |
| 게시글 작성<br>(퀴즈 인증) | 학사일정 | 설정 |
| ![게시글 작성](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/9010867d-5c5c-468a-b039-52f3a41692eb) | ![학사일정](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/59227acf-a226-41ac-a73d-d863cde3c4bc) | ![설정](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/0ebe317a-ac9e-4d3f-a339-9e62479feb52) |
| 플레이스토어 | | |
| ![플레이스토어](https://github.com/Neibce/Dongcheon-Alimi/assets/18096595/9f712c9d-84a2-4af9-b06e-8984460484f3) | | |

## 결과

플레이스토어에 배포 후 교내 홍보를 진행했습니다. 학생들의 반응이 예상보다 좋았고, 특히 **건의사항 게시판**에 대한 호평이 많았습니다. 기존에는 학교생활 불편사항을 건의할 방법이 없었는데, 이 앱을 통해 직접 건의할 수 있게 되었기 때문입니다. 앱을 통해 올라온 건의사항들은 실제로 학생회에 반영되어 학교 환경이 개선되는 성과를 얻었습니다.

## 사용 기술

- Android (Java)
- Material Design
- NEIS 크롤링 (정규표현식)
- Firebase Cloud Messaging
