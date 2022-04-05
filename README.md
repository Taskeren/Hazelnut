# Hazelnut

A bot to provide services for Destiny 2 players in Kaiheila.   
All assets from [Bungie](https://www.bungie.net) are for non-commercial usage, and whose rights reserved by Bungie.

Data are from [Bungie API](https://www.bungie.net/7/en/registration) and stored in local. 
So the things that are not available in the API(_light.gg_ marks them as _Classified_) are not available in the bot as well.
_By the way, if you would like to update your data cache, you should perform '/d2 update' command._

__As a private bot, some features have nothing to do with Destiny 2 are possibly added.__

__The permission system are not prepared to work, so the commands with heavy process are able being called by anyone and causing overloads. USE AT YOUR OWN RISKS__

## License 

Codes are licensed under MIT.

## Usage

1. Run the Jar in console. And for the first time with no configurations, it should just create a default configuration file and exit.
2. Change the values of the configuration where 'botToken' are from [Kaiheila Developer](https://developer.kaiheila.cn/app/index) and the connection type should be 'WebSocket'.
3. Run the Jar once again, and if you see `客户端启动成功，开始监听！`, it runs properly.