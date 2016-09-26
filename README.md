# taichi
Custom trust association interceptor for WebSphere for REMOTE_USER. This allows a reverse proxy to authenticate a user independently, and just provide WebSphere with REMOTE_USER

Note: this is very insecure. You need to ensure the proxy is trusted, is stripping and/or replacing REMOTE_USER from the headers provided by a user's browser, and that ALL access to WebSphere goes through the reverse proxy.

## Install on a WebSphere server
* Check the repository out to a directory
* Run `gradlew build`
* Copy `.\build\libs\taichi-1.0.jar` to `/opt/IBM/WebSphere/AppServer/lib/ext/`
* Log in to WebSphere Console
* Go to *Security -> Global Security*
* Select the checkbox for *Enable application security* and hit *Apply*
* Go to -> Expand Web and SIP Security -> Trust association*
* Select the checkbox for *Enable trust association* and hit *Apply*
* Click on *Interceptors* then *New...*
* The class name is `com.dtec.taichi.RemoteUserTrustAssociationInterceptor`
* For increased security: under *Custom properties*, enter `proxy` for the name, and the IP address of the reverse proxy (Apache HTTPD or IIS) in the Value field.
* Click *Okay* and select to *Save* the changes
* Restart WebSphere

## Test
* Check the repository out to a directory
* Run `gradlew build`
* `docker run --name websphere -p 9043:9043 -p 9443:9443 -d ibmcom/websphere-traditional:profile`
* `cd ./build/libs && docker cp taichi-1.0.jar websphere:/opt/IBM/WebSphere/AppServer/lib/ext/`
* To get the admin password, run `docker exec websphere cat /tmp/PASSWORD`
* (It will take some time for WebSphere to start. Run `docker logs -f websphere` to tail the logs to see when it starts.)
* After the server has started, go to `https://localhost:9043/ibm/console` (you may need to substitute localhost with the virtual machine IP for Docker - try `docker-machine env default` to get the IP on Windows, for example)
* Log in as `wsadmin`, using the password from the command a few steps back.
* Go to *Security -> Global Security*
* Select the checkbox for *Enable application security* and hit *Apply*
* Go to -> Expand Web and SIP Security -> Trust association*
* Select the checkbox for *Enable trust association* and hit *Apply*
* Click on *Interceptors* then *New...*
* The class name is `com.dtec.taichi.RemoteUserTrustAssociationInterceptor`
* Click *Okay* and select to *Save* the changes
* Restart WebSphere. Run `docker exec -it kill -s INT 1`. After a few seconds `docker ps` will no longer list the container. Run `docker start websphere` to bring it back up. It will take a minute to fully start up.
* You need to install a Chrome plugin to alter your request headers. Once you have done that, enable it to run in incognito mode and then add the header `REMOTE_USER` value `wsadmin` for `localhost` (or your Docker virtual machine IP)
* In an incognito window (Chrome). go to `https://localhost:9443/snoop` (substitute localhost if required)
* Verify that `wsadmin` shows up in the `User Principal`. You can also experiment with different headers and users (the user must exist in WebSphere)
