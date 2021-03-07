# Auditchain

Auditchain is a CLI application that uses [Opentimestamps](https://opentimestamps.org/) to perform Proof of Existence from logs registered in Elasticsearch databases.

## Requirements

You will need to [install libsodium](https://libsodium.gitbook.io/doc/installation) on your system and have a [key pair](https://libsodium.gitbook.io/doc/public-key_cryptography/public-key_signatures) to sign and verify data.

## Installation

Download the latest release.

Create a config.properties file in the same path as .jar with the following information:

```
# Elasticsearch infos
elasticHost = https://host.of.your.elasticsearch.com/
elasticUser = your-user
elasticPwds = your-password
# separate indexPatterns with commas
indexPatterns = indexPattern1*, indexPattern2*, indexPatternN*

# Attestation infos
# frequency in minutes 
frequency = 25
# delay in seconds
delay = 10
# maxTimeInterval in hours
maxTimeInterval = 24
# keys to sign data
signingKey = path/to/your/signingKey
verifyKey = path/to/your/verifyKey
```

## Usage

To create a new Proof of Existence from logs of a certain period, execute:

```
java -jar Auditchain-X.X.X-RELEASE.jar stamp-elasticsearch -v --start-at="aaaa-MM-dd hh:mm" --finish-in="aaaa-MM-dd hh:mm"
```

To verify a new Proof of Existence from logs of a certain period, execute:

```
java -jar Auditchain-X.X.X-RELEASE.jar verify-elasticsearch -v --start-at="aaaa-MM-dd hh:mm" --finish-in="aaaa-MM-dd hh:mm"
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## Support
For any questions, contact me at bruno.mend94@gmail.com

## License
GNU GPLv3
