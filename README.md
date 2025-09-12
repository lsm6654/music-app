# music-app project

## requirements
- 데이터 사용: https://www.kaggle.com/datasets/devdope/900k-spotify?select=900k+Definitive+Spotify+Dataset.json 파일을 다운로드하여 기초 데이터로 사용하세요.
- 데이터 처리: 기초 데이터 파일을 효율적으로 읽어 관계형 데이터베이스에 저장하는 애플리케이션을 개발하세요.
  - 메모리 사용량을 최소화해야 합니다. 
  - 인덱싱 전략을 고려해야 합니다.
- API 구현: 아래 조건을 만족하는 Restful 한 API를 구현하세요. 
  - 연도 & 가수별 발매 앨범 수 조회 API: 페이징 기능을 포함합니다. 
  - 노래별 '좋아요' API:
    - '좋아요' 기능에 대한 모델링을 포함합니다.
    - '좋아요' 를 증가시킬 수 있는 API를 구현합니다. 
    - 최근 1시간 동안 '좋아요' 증가 Top 10을 확인할 수 있는 API를 구현합니다.
- PR : feature 별 PR 을 작성해주세요. 
- 테스트 코드: 충분한 테스트 코드를 작성해주세요.
