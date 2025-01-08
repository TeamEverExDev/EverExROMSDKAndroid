# Android SDK 연동 규격서

> (본 가이드는 .kts 확장자의 build Script 기준으로 작성되었습니다.)  
> (SDK를 탑재할 프로젝트가 Android Studio Bumblebee (2021.1.1) 이전에 만들어진 프로젝트라면 .kt 확장자의 build script로 변경 작업을 하셔야 합니다.)

## 빌드 버전 요건
- min sdk version 27 이상 필요

## 설치 가이드

[android sdk library 설치 가이드](./guide/설치가이드.md)
   
## 연동 가이드

1. **측정 부위 선택하기**

해당 프로젝트는 대분류(팔, 팔꿈치, 척추, 무릎)와 해당 부위에 따른 소분류 운동으로 나누어져 있습니다.

1) 대분류 호출하기
   - 대분류는 패키지의 MeasurePart enum 형태로 정의되어 있습니다.
      ```kotlin
            enum class MeasurePart(val exerciseName : String) {
                  STATIC("정적 자세"),
                  SHOULDER("어깨"),
                  ELBOW("팔꿈치"),
                  SPINE("몸통"),
                  KNEE("무릎"),
                  NECK("목"),
                  HIP_JOINT("고관절");
            }
      ```
   - 따라서, enum을 호출해서 원하는 측정의 대분류를 호출 할 수 있습니다.
      ```kotlin
         // 전체 부위 호출 예시
         val measureList : List<MeasurePart> = MeasurePart.entries 
     
        // 정적 자세 호출 예시
        val staticMeasure = MeasurePart.STATIC
     
        // 어깨 호출 예시
        val shoulderMeasure = MeasurePart.SHOULDER
     
        // 팔꿈치 호출 예시
        val elbowMeasure = MeasurePart.SHOULDER

        // 몸통 호출 예시
        val spineMeasure = MeasurePart.SPINE
     
        // 무릎 호출 예시
        val kneeMeasure = MeasurePart.KNEE
     
        // 목 호출 예시
        val neckMeasure = MeasurePart.NECK
     
        // 고관절 호출 예시
        val hipMeasure = MeasurePart.HIP_JOINT
      ```

2) 소분류 선택하기
   * 대분류를 통해 자세 측정 목록 호출 매서드 예시
     - 1)에서 호출한 대분류 값을 매개변수로 enum을 호출합니다.
     ```kotlin
         fun selectDetailROM(rom : MeasurePart) : List<Measurable> {
             return when(rom) {
                 MeasurePart.STATIC -> StaticMeasure.entries
                 MeasurePart.SPINE -> SpineMeasure.entries
                 MeasurePart.SHOULDER -> ShoulderMeasure.entries
                 MeasurePart.KNEE -> KneeMeasure.entries
                 MeasurePart.ELBOW -> ElbowMeasure.entries
                 MeasurePart.NECK -> NeckMeasure.entries
                 MeasurePart.HIP_JOINT -> HipJointMeasure.entries
             }
         }
     ```

   * 정적 자세 촬영하기
   [android 정적 자세 촬영 연동 가이드](./guide/정적자세가이드.md)
   * 동적 자세 촬영하기
   [android ROM 촬영 연동 가이드](./guide/ROM촬영가이드.md)