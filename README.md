
![Pokemon Logo](https://raw.githubusercontent.com/capside/azure-mesos-pokemon/master/pokemon.png)

# Mesos clusters with Pokémon

## Prerequisites

* You will need the *cli* in order to interact with Auzre. Install  [nodejs](https://nodejs.org/en/) and then execute  ```npm install -g azure-cli```
* If you don't have a keypair stored in your system you can generate them using ```ssh-keygen -t rsa -b 2048 -C "email@dominio.com"```. **DON'T OVERWRITE ANY PREVIOUSLY CREATED KEYS**.
* Lastly you can download this project by typing ```git clone https://github.com/capside/azure-mesos-pokemon.git``` and ```cd azure-mesos-pokemon```

## Cluster creation

* *CLI* configuration

```bash
az login
``` 
* Ensure your subscription is correctly activated

```bash
az account list --output table
az account set --subscription "<subscription name from above>"
``` 

* If this is your first time with that account you will need to register the needed services

```bash
az provider register --namespace Microsoft.Network
az provider register --namespace Microsoft.Storage
az provider register --namespace Microsoft.Compute
az provider register --namespace Microsoft.ContainerService
``` 

* Check if your quota of CPUs is big enough

```bash
az vm list-usage --location westeurope --output table --query [*].[currentValue,limit,name.value]
```

* Define some environment variables

```bash
ADMIN_USERNAME=<your_username>
RESOURCE_GROUP=<a_logical_name>
DEPLOYMENT_NAME=<name_of_the_deployment>
ACS_NAME=containerservice-$RESOURCE_GROUP
LOCATION=westeurope
TEMPLATE_URI=https://raw.githubusercontent.com/Azure/azure-quickstart-templates/master/101-acs-dcos/azuredeploy.json
PARAMFILE=azuredeploy.parameters.json
```

* **EDIT azuredeploy.parameters.json** and set the desired parameters
* TIP: On Mac you can use ```pbcopy < ~/.ssh/id_rsa.pub``` to send the public key to the clipboard (you have to paste it as the last parameter of the file).

* Deploy the cluster to the *resource group*

```bash
cd azure-arm
az group create --name $RESOURCE_GROUP --location $LOCATION
az group deployment create --resource-group $RESOURCE_GROUP --template-uri $TEMPLATE_URI --parameters @$PARAMFILE --name $DEPLOYMENT_NAME --no-wait

az group deployment show --resource-group $RESOURCE_GROUP --name $DEPLOYMENT_NAME --output json | grep State
```

## Manage the cluster using the web IU

* Establish an ssh tunnel between your laptop and one master

```
MASTER="$RESOURCE_GROUP"mgmt.westeurope.cloudapp.azure.com
AGENTS="$RESOURCE_GROUP"agents.westeurope.cloudapp.azure.com
ssh -L 8000:localhost:80 -N "$ADMIN_USERNAME"@"$MASTER" -p 2200 &
```

* Note if you are running these commands on a remote system you will also need to establish a tunnel from your laptop to the remote system (as we'll use this tunnel to browse locally the web interface).

```
ssh -L 8000:localhost:8000 -N <user>@<remote_system>
```

* Open [http://localhost:8000](http://localhost:8000) in your local browser

## Manage Mesos

* Open [http://localhost:8000/mesos](http://localhost:8000/mesos) 

## Manage Marathon

* Open [http://localhost:8000/mesos](http://localhost:8000/mesos) 

## Check the master node (optional)

* You can check that the master node IP matches the one shown by the web IU

```
ssh "$ADMIN_USERNAME"@"$MASTER" -p 2200
ifconfig | grep "inet addr"
```

## Increase the number of instances in the public VMSS

* List the deployed VMSS

```
az resource list --resource-group $RESOURCE_GROUP --resource-type Microsoft.Compute/virtualMachineScaleSets
``` 

* Identify the public VMSS (property *name*, for example "dcos-agent-public-2D554AAB-vmss0")
* Modify the number of instances on that VMSS

```
PUBLIC_AGENTS_VMSS=<the name of the public vmss>
az vmss scale --resource-group $RESOURCE_GROUP --name $PUBLIC_AGENTS_VMSS --new-capacity 3
```

## Deploy your Pokémon!

* Install prettyjson with ```npm install -g prettyjson```
* Take a look to the deployment descriptor in the file [deploy-pokemon.json](https://github.com/capside/azure-mesos-pokemon/blob/master/azure-arm/deploy-pokemon.json)
* Reveiew the [Marathon API](https://mesosphere.github.io/marathon/docs/rest-api.html) (optional)
* Execute a HTTP request to deploy the application:

```
curl -X POST http://localhost:8000/marathon/v2/apps -d @deploy-pokemon.json -H "Content-type: application/json" | prettyjson
```

## Visualize the deployed containers

* Check again the [Marathon UI](http://localhost:8000/marathon)
* Access the application using your browser with http://"$AGENTS":8080
* Use the API to list the deployed applications

```
curl -s http://localhost:8000/marathon/v2/apps | prettyjson | grep instances
```

## Scaling containers

* Scale up the number of containers using the API

```
curl -X PUT -d "{ \"instances\": 3 }" -H "Content-type: application/json" http://localhost:8000/marathon/v2/apps/poki
```

## Check the resilience of the containers

* Reload the app in your browser ```http://"$AGENTS":8080```
* Click over the image of one of the Pokémon
* Visualise how the container disappears [http://localhost:8000/#/services/%2Fpoki/](http://localhost:8000/#/services/%2Fpoki/)
* In a few seconds a new container should be respawn

## Using the DC-OS CLI (optional)

* Download and install the cli from [official site](https://dcos.io/docs/1.8/usage/cli/install/#windows)
* Configure the API endpoint with ```dcos config set core.dcos_url http://localhost:8000```
* Try to list the deployed apps with ```dcos marathon app list```

## Clean up (**IMPORTANT**)

```
az group delete --name $RESOURCE_GROUP 
``` 
 
 
 
