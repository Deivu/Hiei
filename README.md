# Hiei

<p align="center">
  <img src="https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/images/skins/205/Moonlit_Cruise/image.png">
</p>

The ShipGirl Project, Hiei; `(c) Azur Lane`

## Features

✅ Easy to use

✅ Configurable 

✅ Rest based API

✅ Automatic Updates

## Downloads

🔗 ...

## Support

🔗 https://discord.com/invite/FVqbtGu (#development)

🔗 https://discord.com/invite/aAEdys8 (#support)

Ping or ask for `@Sāya#0113`

## How to host 

> Download the latest version from Github Releases

> Copy a config from [examples](https://github.com/Deivu/Hiei/tree/master/example) folder

> Run the server by doing `java -jar hiei.jar`


## Rest Client Example
> Node.JS (Javascript)
```js
const Fetch = require('node-fetch');

class AzurLane {
    constructor() {
        this.baseURL = 'http://localhost:1024';
        this.auth = '1234';
    }

    searchShip(ship) {
        return this._fetch('/ship/search', ship);
    }

    _fetch(endpoint, q) {
        const url = new URL(endpoint, this.baseURL);
        url.search = new URLSearchParams({ q }).toString();
        return Fetch(url.toString(), { headers: { 'authorization': this.auth } })
            .then(data => data.json());
    }
}

const client = new AzurLane();
client.searchShip('hiei')
    .then(data => data.json())
    .then(data => console.log(data));
```

> Made with ❤ by @Sāya#0113