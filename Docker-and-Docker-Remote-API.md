## In order to install Docker and enable Docker's Remote API on a Raspberry Pi 3:
* Execute `sudo apt-get install -y apt-transport-https`.
* Execute `wget -q https://packagecloud.io/gpg.key -O - | sudo apt-key add -`.
* Execute `echo 'deb https://packagecloud.io/Hypriot/Schatzkiste/debian/ wheezy main' | sudo tee /etc/apt/sources.list.d/hypriot.list`.
* Execute `sudo apt-get update`.
* Execute `sudo apt-get install -y docker-hypriot`.
* Execute `sudo systemctl enable docker`.
* Execute `sudo docker version` to confirm that Docker works well.
* Execute `sudo service docker stop`.
* Execute `cd /etc/systemd/system/`.
* Execute `sudo mkdir docker.service.d`.
* Execute `cd docker.service.d`.
* Execute `sudo nano remote-api.conf`.
* Add these three lines to the file:<br/>
`[Service]`<br/>
`ExecStart=`<br/>
`ExecStart=/usr/bin/docker daemon -H tcp://0.0.0.0:4243 -H unix:///var/run/docker.sock`<br/>
* Execute `sudo systemctl daemon-reload`.
* Execute `sudo service docker restart`.
* Execute `sudo curl -X GET http://127.0.0.1:4243/images/json` to make sure that Docker Remote API works well, and you do not receive any error message.

## In order to install Docker and enable Docker's Remote API on a cloud-based host:
* Execute `apt-get update`. This command downloads the package lists from the repositories and updates them to get information on the newest versions of packages and their dependencies.
* Execute `apt-get -y install docker.io`. This command installs Docker.
* Execute `ln -sf /usr/bin/docker.io /usr/local/bin/docker`. This command links Docker path.
* Execute `service docker stop`. This command stops Docker engine.
* Add this line `DOCKER_OPTS='-H tcp://0.0.0.0:4243 -H unix:///var/run/docker.sock'` to this file `/etc/default/docker`.
* Execute `service docker start`. This command starts Docker engine again.
* Execute `curl -X GET http://127.0.0.1:4243/images/json` to make sure that Docker Remote API works well, and you do not receive any error message.
