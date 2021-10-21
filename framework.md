# Framework 

[MVC、MVP、MVVM](https://ithelp.ithome.com.tw/articles/10218263)  




## MVC

![圖 3](images/f3a0037ca9e77019a531c4f07fed85a279f6ecff2ce545c33635c5ce8ca7f5d7.png)  

```
User -> -> View -> Controller
Controller -> Model -> View -> User
```




## MVP

![圖 4](images/43b805013bb31f005cfd67bd26579c95194d9972184b347b86e56b7c68943789.png)  

```
User -> View -> Controller
Controller -> Presenter -> Model -> Presenter -> View
```


## MVVM

![圖 5](images/fe311ae00ffb1ba6808ebfa89af3ed07bf5df8787b683a275aa2020bd826c934.png)  

Model：管理資料來源如API和本地資料庫
View：顯示UI和接收使用者動作
ViewModel：從Model取得View所需的資料

透過觀察者模式將 View 和 Model 巧妙地連接在一起，一旦 Model 的數據發生變化，觀察者 View 就能夠感應到這個更動，並把數據更新到 UI 畫面上，ViewModel 甚至不需要持有 View 的引用，更方便進行單元測試。


- 大幅減少代碼量，省去了 MVP 中用來連接彼此的介面，Model 層數據更新後也不必透過介面 callback 給 view，因為 View 會透過 observe 感知數據的變動並更新畫面。

- 可以搭配 DataBinding、LiveData 等框架使用，能更方便地處理 UI 的更新，生命週期的處理。
ViewModel 能夠輕易地保存數據，且可以被多個 View 共享(MVC、MVP 也可以但較不適合)，View 與 View 之間傳遞數據也更方便(只有 MVVM 可以)。

- 撰寫測試時，MVP 需要 Mock 一個 View 對象才能進行測試，由於 ViewModel 不需持有 View 的引用，更方便進行單元測試。