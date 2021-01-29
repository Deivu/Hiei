# Configurations

> pass: sets the authorization for this api, `/update` endpoint will always be auth protected

> port: Defaults to `1024`. Port for this API

> threads: Defaults to `your core count`. Amount of threads for the API to use

> routePrefix: Defaults to `/`. Mainly used for reverse proxy

> privateRest: Defaults to `true`. If true, the ship and equipment endpoints will be auth protected. `/update` will always be auth protected whatever this is set to

> checkUpdateInterval: Defaults to `0`. 0 means disabled, setting this to any value other than 0 enables it. This is measured in "hour". ex: setting this to 1 will make the api check for update every 1 hour

> maxResults: Defaults to `5`. Returns how many results the api can return in one endpoint, provided if that endpoint returns an JSONarray

> editDistance: Defaults to `6`. The higher this is, the more forgiving `search` endpoints is

## config.json

> This config contains the only config needed to run this server. The unspecified keys will default to their default values

## config[complete].json

> This config contains all the possible configuration for this server

### Note: the file name must be `config.json` beside the `.jar` file