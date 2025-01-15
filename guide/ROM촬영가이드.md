## ROM 촬영 가이드

0) ROM 촬영이 가능한 자세는 다음과 같습니다.
   - `ShoulderMeasure`, `ElbowMeasure`, `SpineMeasure`, `KneeMeasure`, `NeckMeasure`, `HipJointMeasure`
     - 각 자세에 대한 세부 촬영 항목은 다음과 같습니다.
       * `ShoulderMeasure`
         ```kotlin
            enum class ShoulderMeasure(override val exerciseName: String) {
                LEFT_SIDE_LATERAL("왼팔 옆으로 벌리기"),
                RIGHT_SIDE_LATERAL("오른팔 옆으로 벌리기"),
                RIGHT_ARM_BENT("오른팔 앞으로 올리기"),
                RIGHT_ARM_LOOSE("오른팔 뒤로 펴기"),
                LEFT_ARM_BENT("왼팔 앞으로 올리기"),
                LEFT_ARM_LOOSE("왼팔 뒤로 펴기");
            }
         ```
       * `ElbowMeasure`
         ```kotlin
            enum class ElbowMeasure(override val exerciseName : String) {
               LEFT_ELBOW_BENT("왼쪽 팔꿈치 굽히기"),
               LEFT_ELBOW_LOOSE("왼쪽 팔꿈치 펴기"),
               RIGHT_ELBOW_BENT("오른쪽 팔꿈치 굽히기"),
               RIGHT_ELBOW_LOOSE("오른쪽 팔꿈치 펴기");
            }
         ```  
       * `SpineMeasure`
         ```kotlin
            enum class SpineMeasure(override val exerciseName : String) {
                TRUNK_BENT_TO_FORWARD("허리 앞으로 숙이기"),
                TRUNK_BENT_TO_BACKWARD("허리 뒤로 젖히기"),
                SPINE_BENT_TO_LEFT("허리 왼쪽으로 굽히기"),
                SPINE_BENT_TO_RIGHT("허리 오른쪽으로 굽히기");
            }
         ```
       * `KneeMeasure`
         ```kotlin
            enum class KneeMeasure(override val exerciseName : String) {
                    LEFT_KNEE_BENT("왼쪽 무릎 굽히기"),
                    LEFT_KNEE_LOOSE("왼쪽 무릎 펴기"),
                    RIGHT_KNEE_BENT("오른쪽 무릎 굽히기"),
                    RIGHT_KNEE_LOOSE("오른쪽 무릎 펴기");
            }
         ```  
       * `NeckMeasure`
         ```kotlin
            enum class NeckMeasure(override val exerciseName : String) {
                BENT_NECK_TO_LEFT("목 왼쪽으로 굽히기"),
                BENT_NECK_TO_RIGHT("목 오른쪽으로 굽히기");
            }
         ```
       * `HipJointMeasure`
         ```kotlin
            enum class HipJointMeasure(override val exerciseName : String) {
                BENT_LEFT_HIP("왼쪽 고관절 굽히기"),
                LOOSE_LEFT_HIP("왼쪽 고관절 펴기"),
                BENT_RIGHT_HIP("오른쪽 고관절 굽히기"),
                LOOSE_RIGHT_HIP("오른쪽 고관절 펴기");
            }
         ```    


1) 필요한 ROM 촬영 리스트를 만듭니다.
   ```kotlin
        val romMeasureList = listOf(
            ShoulderMeasure.LEFT_SIDE_LATERAL,
            ShoulderMeasure.RIGHT_SIDE_LATERAL,
            NeckMeasure.BENT_NECK_TO_LEFT,
            NeckMeasure.BENT_NECK_TO_RIGHT,
            ElbowMeasure.LEFT_ELBOW_BENT,
            ElbowMeasure.RIGHT_ELBOW_BENT,
        )
   ```  
   
2) 유저 정보를 담은 객체를 생성합니다. Data Class가 정의되어 있으니 해당 클래스를 호출하여주십시오.
    <br> UUID Document : https://developer.android.com/reference/java/util/UUID
   ```kotlin
        val userInfo = UserInfo(
            uid = UUID.randomUUID() //uuid 형식에 맞추어 아이디를 넣어주세요.
        )
   ```  
   
3) 위에서 정의한 측정 리스트와 유저 정보 객체를 가지고 카메라를 호출합니다.
   ```kotlin
        enterEverExROMSDK(context, staticMeasureList, userInfo, callback)
   ``` 
   
4) 콜백을 정의합니다. 
    - 콜백은 `List<ResultWrapper>`와 `ErrorStatus` 두 개 중 하나로 리턴됩니다.
    - `List<ResultWrapper>`가 not null 이라면 sdk가 올바른 리턴 값을 리턴한 것입니다. `ROMTestResultWrapper`로 casting 하여 사용합니다.
    ```kotlin
        @Parcelize
        data class ROMTestResultWrapper(
            override val name: String,
            val angleInDegree: Float?,
            val valueType: ResultType
        ) : Parcelable, ResultWrapper {
            enum class ResultType {
                MIN, MAX 
            }
        }
    ``` 
    - `ErrorStatus`가 not null 이라면 에러가 리턴된 것입니다. 정의된 에러를 보고 형식에 맞추어 재호출 하거나, 개발사로 문의 주시면 됩니다.
    ```kotlin
        enum class ErrorStatus {
            UNAUTHORIZED_CLIENT, // 해당 에러는 인증 에러입니다. 공급사 측으로 문의 부탁드립니다.
            INVALID_PARAMETER, // 잘못된 파라메터를 입력하였을 시 발생합니다. 유저정보나 리스트의 값을 확인 후 재호출을 시도하십시오.
            NOT_FOUND_MEASURES, // 측정부위가 존재하지 않거나, 사용할 수 없는 측정부위를 넣었을 시 발생합니다.
            SERVER_ERROR, // 서버에러 발생 시 발생하는 에러입니다.
            UNKNOWN_ERROR // 이외의 에러입니다. 공급사 측에 문의 부탁드립니다.
        }
    ```
   
    <br>example)
    ```kotlin
        enterEverExROMSDK(context, staticMeasureList, userInfo) { resultList, error ->
            // 콜백 정의 부분
   
            // 결과값 리턴
            if(resultList != null) {
                val resultWrapperList = resultList.filterIsInstance<ROMTestResultWrapper>()
                for(data in resultWrapperList) {
                    var name = data.name
                    val degree = data.angleInDegree
                    val maxOrMin = data.valueType.let { type ->
                        when(type) {
                            ROMTestResultWrapper.ResultType.MAX -> "Maximum Angle"
                            ROMTestResultWrapper.ResultType.MIN -> "Minimum Angle"
                        }
                    }
                }
            }      
   
            // 에러값 리턴
            if(error != null) { 
                when(error) {
                    ErrorStatus.UNAUTHORIZED_CLIENT -> {
                        ErrorStatus.UNAUTHORIZED_CLIENT
                        //todo :- 인증 오류에 따른 에러 처리
                    }
   
                    ErrorStatus.INVALID_PARAMETER -> {
                        ErrorStatus.INVALID_PARAMETER
                        //todo :- 잘못된 파라메터에 대한 오류에 따른 에러 처리
                    }
   
                    ErrorStatus.NOT_FOUND_MEASURES -> {
                        ErrorStatus.NOT_FOUND_MEASURES
                        //todo :- 잘못된 부위 사용에 따른 에러 처리
                    }
   
                    ErrorStatus.SERVER_ERROR -> {
                        ErrorStatus.SERVER_ERROR
                        //todo :- 서버 오류에 따른 에러 처리
                    }

                    ErrorStatus.UNKNOWN_ERROR -> {
                        ErrorStatus.UNKNOWN_ERROR
                        //todo :- 알 수 없는 오류에 따른 에러 처리
                    }
                }      
            }
        }
    ``` 
