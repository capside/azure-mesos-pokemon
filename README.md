
![Pokemon Logo](http://vignette1.wikia.nocookie.net/es.pokemon/images/6/61/Logo_de_Pok%C3%A9mon_(EN).png)

# Mesos clusters with Pokémon

## Prerequisites

* You will need the *cli* in order to interact with Auzre. Install  [nodejs](https://nodejs.org/en/) and then execute  ```npm install -g azure-cli``` or use the installer: https://docs.microsoft.com/en-us/azure/xplat-cli-install#option-2-use-an-installer
* You will also need *ssh* in your system. If you are a Windows user the easier way to install it is [git for Windows](https://git-scm.com/download/win).
* If you don't have a keypair stored in your system you can generate them using ```ssh-keygen -t rsa -b 2048 -C "email@dominio.com"```. **DON'T OVERWRITE ANY PREVIOUSLY CREATED KEYS**.
* Lastly you can download this project by typing ```git clone https://github.com/capside/azure-mesos-pokemon.git``` and ```cd azure-mesos-pokemon```

## Cluster creation

* *CLI* configuration

```bash
azure config mode arm
azure login
```
* Ensure your subscription is correctly activated

```bash
azure account list
azure account set <subscription_number>
```

* If this is your first time with that account you will need to register the needed services

```bash
azure provider register --namespace Microsoft.Network
azure provider register --namespace Microsoft.Storage
azure provider register --namespace Microsoft.Compute
azure provider register --namespace Microsoft.ContainerService
```

* Check if your quota of CPUs is big enough

```bash
azure vm list-usage --location westeurope
```

* Define some environment variables

```bash
set ADMIN_USERNAME=<your_username>
set RESOURCE_GROUP=<a_logical_name>
set DEPLOYMENT_NAME=<name_of_the_deployment>
set ACS_NAME=containerservice-%RESOURCE_GROUP%
set LOCATION=westeurope
set TEMPLATE_URI=https://raw.githubusercontent.com/Azure/azure-quickstart-templates/master/101-acs-dcos/azuredeploy.json
set PARAMFILE=azuredeploy.parameters.json
```

* **EDIT azuredeploy.parameters.json** and set the desired parameters
* TIP: On Windows you can use ```type id_rsa.pub | clip``` to send the public key to the clipboard (you have to paste it as the last parameter of the file).

* Deploy the cluster to the *resource group*

```bash
cd azure-arm
azure group create -n %RESOURCE_GROUP% -l %LOCATION% --template-uri %TEMPLATE_URI% -e %PARAMFILE% --deployment-name %DEPLOYMENT_NAME%

azure group deployment show %RESOURCE_GROUP% %DEPLOYMENT_NAME% | findstr State
```

## Manage the cluster using the web IU

* Establish an ssh tunnel between your laptop and one master

```bash
set MASTER=%RESOURCE_GROUP%mgmt.westeurope.cloudapp.azure.com
set AGENTS=%RESOURCE_GROUP%agents.westeurope.cloudapp.azure.com
start ssh -L 8000:localhost:80 -N %ADMIN_USERNAME%@%MASTER% -p 2200
```

* Open [http://localhost:8000](http://localhost:8000) in your local browser

## Manage Mesos

* Open [http://localhost:8000/mesos](http://localhost:8000/mesos) 

## Manage Marathon

* Open [http://localhost:8000/marathon](http://localhost:8000/marathon) 

## Check the master node (optional)

* You can check that the master node IP matches the one shown by the web IU

```
ssh %ADMIN_USERNAME%@%MASTER% -p 2200
ifconfig | grep "inet addr"
```

## Increase the number of instances in the public VMSS

* List the deployed VMSS

```
azure resource list %RESOURCE_GROUP% --resource-type Microsoft.Compute/virtualMachineScaleSets --json  
```

* Identify the public VMSS (property *name*, for example "dcos-agent-public-2D554AAB-vmss0")
* Modify the number of instances on that VMSS

```
set PUBLIC_AGENTS_VMSS=<name_of_the_public_VMSS>
azure vmss scale --resource-group %RESOURCE_GROUP% --name %PUBLIC_AGENTS_VMSS% --new-capacity 3
```

## Deploy your Pokémon!

* If you are running windows, install [curl](https://curl.haxx.se/download.html)
* Take a look to the deployment descriptor in the file [deploy-pokemon.json](https://github.com/capside/azure-mesos-pokemon/blob/master/azure-arm/deploy-pokemon.json)
* Reveiew the [Marathon API](https://mesosphere.github.io/marathon/docs/rest-api.html) (optional)
* Execute a HTTP request to deploy the application:

```
curl -X POST http://localhost:8000/marathon/v2/apps -d @deploy-pokemon.json -H "Content-type: application/json"
```

## Visualize the deployed containers

* Check again the [Marathon UI](http://localhost:8000/marathon)
* Access the application using your browser with ```start http://%AGENTS%:8080```
* Use the API to list the deployed applications

```
curl -s http://localhost:8000/marathon/v2/apps | prettyjson | grep instances
```

## Scaling containers

* Scale up the number of containers using the API

```
curl -X PUT -d "{ \"instances\": 3 }" -H "Content-type: application/json" http://localhost/marathon/v2/apps/poki
```

## Check the resilience of the containers

* Reload the app in your browser ```start http://%AGENTS%:8080```
* Click over the image of one of the Pokémon
* Visualise how the container disappears ```http://localhost:8000/#/services/%2Fpoki/```
* In a few seconds a new container should be respawn

## Using the DC-OS CLI (optional)

* Download and install the cli from [official site](https://dcos.io/docs/1.8/usage/cli/install/#windows)
* Configure the API endpoint with ```dcos config set core.dcos_url http://localhost:8000```
* Try to list the deployed apps with ```dcos marathon app list```

## Clean up (**IMPORTANT**)

```
azure group delete --name %RESOURCE_GROUP%
```
