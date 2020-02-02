# [Noktis][noktis-download] – A dating app

## What is Noktis?
Noktis is an application created in Santa Catarina, Brazil. Noktis was a project for an academic work on Algorithms and Programming II in late 2019 (developed between early november and early december) at the Universidade do Vale do Itajaí. The project name comes from the word "Noctis" which means "night" in the ancient Latin language.

As I had great plans and ideas for this academic project and the deadline of a month could be short to develop the [API][meu-fork-api] and the application totally alone and by myself, I joined efforts with one of my classmates, [Gustavo Constantini][constantini-github], so I was in charge of developing this Java application for Android while [he][constantini-github] would develop the API using NodeJS, the project went very well and we managed to implement much of what we wanted into our codebase.

I plan to maintain this project quite often, so it may receive commits in the future to fix bugs, add new features and make improvements.

  Login                               |  Register
:------------------------------------:|:--------------------------------------:
![](https://i.imgur.com/j0y3Z91.png)  |  ![](https://i.imgur.com/32GpBVf.png)
  Birthdate Picker at Register        |  Main settings fragment
![](https://i.imgur.com/1Sc8T8Q.png)  |  ![](https://i.imgur.com/QyAJ7PT.png)
  Someone else's profile              |  Main screen
![](https://i.imgur.com/nUqoiQA.png)  |  ![](https://i.imgur.com/CfOKihh.png)
  Distance limit picker               |  Age range picker
![](https://i.imgur.com/5kj848l.png)  |  ![](https://i.imgur.com/SrDlH61.png)
  Active sessions menu                |  Main settings fragment
![](https://i.imgur.com/wfrW4jw.png)  |  ![](https://i.imgur.com/QyAJ7PT.png)
  My feed (empty)                     |  My feed
![](https://i.imgur.com/nDvKjSu.png)  |  ![](https://i.imgur.com/hufoYfF.png)
  Account settings                    |  Main settings fragment
![](https://i.imgur.com/E4oRwkb.png)  |  ![](https://i.imgur.com/QyAJ7PT.png)


## Steps for a working build
* Download (or git clone) this git repository.
* Open it as a project in the Android Studio IDE.
* If you want to use your own server to host the API, go to app/src/main/java/lucassbeiler/aplicativo/utilitarias/CallsAPI.java and change the enderecoServidor String to match your server IP address and directory scheme.
* Done, now you can modify the source code as you wish and compile your own builds of the Noktis Android client.

###### Feel free to email me (lucasbeiler@protonmail.com) or send me a Telegram message (@lucassbr) if you need.

## Contributing
One can contribute to the project by:
* Reporting bugs and suggesting new features.
* Helping with translations or art (e.g. icons, logos).
* Sending debugging logs when the app crashes on your device.
* Contributing to the code.

You can contribute with this source code by sending your patches the following way:
* Fork this repository.
* Create your own branch.
* Commit your changes.
* Push your changes.
* Submit a new pull request to my repository.

## Used third-party code (libraries)
* Square's Retrofit HTTP lib ([Apache License 2.0](https://github.com/square/retrofit/blob/master/LICENSE.txt))
* Facebook's Fresco image lib ([MIT License](https://github.com/facebook/fresco/blob/master/LICENSE))
* AirLocation ([Source](https://github.com/mumayank/AirLocation))
* CardStackView ([Apache License 2.0](https://github.com/yuyakaido/CardStackView/blob/master/LICENSE))
* RubberPicker ([MIT License](https://github.com/Chrisvin/RubberPicker/blob/master/LICENSE))
* Android-SpinKit ([MIT License](https://github.com/ybq/Android-SpinKit/blob/master/LICENSE))
* BubbleView ([Apache License 2.0](https://github.com/lguipeng/BubbleView#license))
* TastyToast ([Apache License 2.0](https://github.com/yadav-rahul/TastyToast#license))
* Android-Image-Cropper ([Apache License 2.0](https://github.com/ArthurHub/Android-Image-Cropper/blob/master/LICENSE.txt))


[noktis-download]: https://play.google.com/store/apps/details?id=me.lucassbeiler.noctisx
[constantini-github]: https://github.com/GustavoConstantini
[meu-fork-api]: https://github.com/lucassbeiler/noktis_backend_api