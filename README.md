Communications service
=============================================================================

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](http://www.gnu.org/licenses/agpl-3.0)

A service to handle communications - email, sms, push notifications.

Latest Release
------------------


How to use Logistimo SCM
-------------------------

[Knowledge-base ](https://logistimo.freshdesk.com)

Pre-requisites
------------------

* JDK 1.8
* MariaDB 10.1.23
* Redis 
* ActiveMQ 5.14
* Docker

Build Instructions
------------------

To build the artifact and create a docker image of the communications service, run the following commands.

1. Set environment

```
export MAVEN_OPTS=-Xmx718m
export MAVEN_HOME=/opt/apache-maven-3.5.3/
export JAVA_HOME=/opt/java-home
export PATH=$JAVA_HOME/bin:$PATH:$MAVEN_HOME/bin
```

2. Build the artifact

```
mvn clean install
````

3. Build the docker image

```
cd application
```
```
mvn docker:build
```


Mailing Lists
-------------

For broad, opinion based, ask for external resources, debug issues, bugs, contributing to the project, and scenarios, it is recommended you use the community@logistimo.com mailing list.

community@logistimo.com  is for usage questions, help, and announcements.
[subscribe](https://groups.google.com/a/logistimo.com/d/forum/community/join) [unsubscribe](mailto:unsubscribe+community@logistimo.com)

developers@logistimo.com  is for people who want to contribute code to Logistimo.
[subscribe](https://groups.google.com/a/logistimo.com/d/forum/developers/join) [unsubscribe](mailto:unsubscribe+community@logistimo.com)


Firebase credentials files set up instruction
---------------------------------------------

This service facilitates sending push notifications to devices using firebase. In order to enable it, we need to set up firebase credentials files as environment variables. Assuming one is using unix platform to run this service, following commands need to be run

* export GOOGLE_APPLICATION_CREDENTIALS="[PATH]"

* export FIREBASE_CONFIG="[PATH]"

These environment variables defines the firebase credentials file path on server which service can refer to send push notification. For further detail on usage of these variables please refer https://firebase.google.com/docs/guides/

License Terms
---------------------------

This program is part of Logistimo SCM. Copyright © 2018 Logistimo.

Logistimo software is a mobile & web platform for supply chain management and remote temperature monitoring in low-resource settings, made available under the terms of the GNU Affero General Public License (AGPL). 

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.

You can be released from the requirements of the license by purchasing a commercial license. To know more about the commercial license, please contact us at opensource@logistimo.com

Trademarks
----------

Logistimo, Logistimo.com, and the Logistimo logo are trademarks and/or service marks. Users may not make use of the trademarks or service marks without prior written permission. Other trademarks or trade names displayed on this website are the property of their respective trademark owners and subject to the respective owners’ terms of use.


