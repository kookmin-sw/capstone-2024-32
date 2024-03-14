<details>
<summary>📝 커밋 컨벤션</summary>
<div markdown="1">

## 1️⃣ 커밋 컨벤션

커밋을 보낼 때 지켜야할 양식을 의미합니다.
커밋은 제목 + 내용 + (이슈 번호)로 이루어져 있습니다.

<br/>
<br/>

## 2️⃣ 제목

제목은 다음과 같은 방식으로 작성합니다.
```type: title```

type에는 커밋의 종류에 따라 다음과 같이 나뉘어 집니다.


Type | 내용 설명 | 표기법
-- | -- | --
:sparkles: feat  | 기능 추가/수정 등 | ```:sparkles:```
:bug: fix | 버그 수정 | ```:bug:```
:memo: docs | 문서 수정 | ```:memo:```
:bulb: comment | 주석 관련 추가 및 수정 | ```:bulb:```
:pencil2: typo | 간단한 오타 수정 (세미콜론 누락 또는 클래스 이름 오타 등) | ```:pencil2:```
:recycle: refactor | 리팩토링 | ```:recycle:```
:lipstick: assets | UI 관련 assets를 업로드, 수정, 삭제 | ```:lipstick:```
:heavy_plus_sign: dependency | 의존성 관련 | ```:heavy_plus_sign:```
:truck: rename | 파일 혹은 폴더명을 수정하거나 옮기는 작업만인 경우 | ```:truck:```



> :sparkles: feat: User 엔티티 수정

<br/>
<br/>

## 3️⃣ 내용

내용에는 세부적인 변경 내용을 줄마다 -로 시작하는 형태로 적습니다.

```
- User 엔티티에 email, location, phone 필드 추가
- User 엔티티에 Getter, Setter 범위 설정
```

내용은 한 줄 당 72자 내로 작성합니다.
<br/>
내용은 양에 구애받지 않고 최대한 상세히 작성합니다.
<br/>
내용은 어떻게 변경했는지 보다 무엇을 변경했는지 또는 왜 변경했는지를 설명합니다.

<br/><br/>


## 4️⃣ 이슈 번호

이슈 번호에는 만약 해당 커밋이 특정 이슈과 관련된 경우 "유형: #이슈 번호"의 형태로 작성합니다.
<br/>
관련된 이슈가 없다면 생략이 가능합니다.
<br/>
여러 개의 이슈 번호를 적을 때는 쉼표로 구분합니다.
<br/>

유형 | 이슈번호
-- | --
Fixes | 이슈 관련 내용 수정중 (아직 해결되지 않은 경우)
Resolves | 이슈 관련 내용 수정 완료
Ref | 참고할 이슈가 있을 때 사용
Related to | 해당 커밋에 관련된 이슈번호 (아직 해결되지 않은 경우)

> Resolves: #123
> Ref: #456
> Related to: #48, #45

<br/>
<br/>


## 전체 예시

```
:sparkles: feat: 추가 로그인 함수

- 로그인 API 개발

Resolves: #123   
Ref: #456 
```

</div>
</details>


<br/>
<br/>

</details>