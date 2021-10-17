
## Concept Of Reactive

[Note Taking](https://projectreactor.io/docs/core/release/reference/#about-doc)

For Example :::
```java
userService.getFavorites(userId, new Callback<List<String>>() { 
  public void onSuccess(List<String> list) { 
    if (list.isEmpty()) { 
      suggestionService.getSuggestions(new Callback<List<Favorite>>() {
        public void onSuccess(List<Favorite> list) { 
          UiUtils.submitOnUiThread(() -> { 
            list.stream()
                .limit(5)
                .forEach(uiList::show); 
            });
        }

        public void onError(Throwable error) { 
          UiUtils.errorPopup(error);
        }
      });
    } else {
      list.stream() 
          .limit(5)
          .forEach(favId -> favoriteService.getDetails(favId, 
            new Callback<Favorite>() {
              public void onSuccess(Favorite details) {
                UiUtils.submitOnUiThread(() -> uiList.show(details));
              }

              public void onError(Throwable error) {
                UiUtils.errorPopup(error);
              }
            }
          ));
    }
  }

  public void onError(Throwable error) {
    UiUtils.errorPopup(error);
  }
});
```

With Reactive Programming 

```java
serService.getFavorites(userId) 
           .flatMap(favoriteService::getDetails) // a flow of Favorite.
           .switchIfEmpty(suggestionService.getSuggestions()) // first if
           .take(5) // at most, five elements from the resulting flow.
           .publishOn(UiUtils.uiThreadScheduler()) // process each piece of data in the UI thread.
           .subscribe(uiList::show, UiUtils::errorPopup); 
```

