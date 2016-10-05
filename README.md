
![Pokemon Logo](https://raw.githubusercontent.com/capside/azure-mesos-pokemon/master/pokemon.png)

# Clústers Mesos en Azure con Pokémon

## Prerequisitos

* Necesitarás las *cli* para interactuar con Azure. Instala [nodejs](https://nodejs.org/en/) previamente y a continuación  ```npm install -g azure-cli```
* También te hará falta tener una clave RSA. **Solo si no tienes previamente clave generada**: ```ssh-keygen -t rsa -b 2048 -C "email@dominio.com"``` y contesta *enter* a todo.
* Por último descarga este repositorio con ```git clone https://github.com/capside/azure-mesos-pokemon.git``` y ```cd azure-mesos-pokemon```

## Crear un clúster

* Configura las *cli*

```bash
azure config mode arm
azure login
``` 
* Asegúrate de que tienes una suscripción correctamente activada
* Si es tu primera vez es necesario registrar el servicio de contenedores

```bash
azure account list
azure account set <tu_número_de_cuenta>
azure provider register --namespace Microsoft.Network
azure provider register --namespace Microsoft.Storage
azure provider register --namespace Microsoft.Compute
azure provider register --namespace Microsoft.ContainerService
``` 

* Comprueba que tengas cuota suficiente 

```bash
azure vm list-usage --location westeurope
```

* Define las variables de entorno que utilizaremos

```bash
ADMIN_USERNAME=<tu_username>
RESOURCE_GROUP=<un_nombre_lógico>
DEPLOYMENT_NAME=<nombre_del_despliegue>
ACS_NAME=containerservice-$RESOURCE_GROUP
LOCATION=westeurope
TEMPLATE_URI=https://raw.githubusercontent.com/Azure/azure-quickstart-templates/master/101-acs-dcos/azuredeploy.json
PARAMFILE=azuredeploy.parameters.json
```

* En Mac Puedes usar ```pbcopy < ~/.ssh/id_rsa.pub``` para enviar la clave pública al portapapeles (tienes que pegarla como último parámetro del fichero).
* **EDITA azuredeploy.parameters.json** modificando los parámetros correspondientes. 

* Despliega el clúster en el *resource group*

```bash
cd azure-arm
azure group create -n $RESOURCE_GROUP -l $LOCATION --template-uri $TEMPLATE_URI -e $PARAMFILE --deployment-name $DEPLOYMENT_NAME

azure group deployment show $RESOURCE_GROUP $DEPLOYMENT_NAME | grep State
```

## Gestionar clúster mediante web

```
MASTER="$RESOURCE_GROUP"mgmt.westeurope.cloudapp.azure.com
AGENTS="$RESOURCE_GROUP"agents.westeurope.cloudapp.azure.com
ssh -L 8000:localhost:80 -N "$ADMIN_USERNAME"@"$MASTER" -p 2200 &
```

* Si estás usando una terminal remota tendrás que montar un túnel también entre tu laptop y esa terminal:

```
ssh -L 8000:localhost:8000 -N <usuario>@<terminal_remota>
```

* Abre http://localhost:8000 en tu navegador

## Gestionar Mesos

* Abre http://localhost:8000/mesos 


## Administrar master node

Visualizar cómo la IP pública del máster coincide con la que muestra el panel web.

```
ssh "$ADMIN_USERNAME"@"$MASTER" -p 2200
ifconfig | grep "inet addr"
```

## Redefinir el número de instancias en vmss público

* Obtiene los vmss creados

```
azure resource list $RESOURCE_GROUP --resource-type Microsoft.Compute/virtualMachineScaleSets --json  
``` 

* Apunta los nombres del vmss público (propiedad *name*, por ejemplo "dcos-agent-public-2D554AAB-vmss0")
* Aplicar el siguiente paso para modificar el número de instancias públicas

```
PUBLIC_AGENTS_VMSS=<el nombre del vmss público>
azure vmss scale --resource-group $RESOURCE_GROUP --name $PUBLIC_AGENTS_VMSS --new-capacity 3
```

## Desplegar aplicación

* Instalar prettyjson con ```npm install -g prettyjson```
* Si estás con Windows, instalar [curl](https://curl.haxx.se/download.html)
* Explicar la aplicación
* Revisar conceptos de Docker
* Revisar [deploy-pokemon.json](https://github.com/capside/azure-mesos-pokemon/blob/master/azure-arm/deploy-pokemon.json)
* Ver el [API](https://mesosphere.github.io/marathon/docs/rest-api.html) de Marathon

```
curl -X POST http://localhost:8000/marathon/v2/apps -d @deploy-pokemon.json -H "Content-type: application/json" | prettyjson
```

## Visualizar el número de contenedores desplegados

* Visualizar el estado en http://localhost:8000/marathon

* Abre http://"$AGENTS":8080

```
curl -s http://localhost:8000/marathon/v2/apps | prettyjson | grep instances
```

## Modificar el número de contenedores

```
curl -X PUT -d "{ \"instances\": 3 }" -H "Content-type: application/json" http://localhost:8000/marathon/v2/apps/poki
```

## Comprobar la resiliencia de los contenedores

* Recargar la aplicación ```start http://$AGENTS:8080```
* Pulsar sobre uno de los Pokémon
* Visualizar cómo desaparece el contenedor ```start http://localhost/#/nodes/list/``` 
* En unos segundos reaparecerá un nuevo Pokémon 

## Usando la cli de DC-OS

* Descarga e instala las cli para tu plataforma desde el [site oficial](https://dcos.io/docs/1.8/usage/cli/install/)
* Configura el acceso a través de localhost con ```dcos config set core.dcos_url http://localhost```
* Prueba a listar las aplicaciones con ```dcos marathon app list```

## Limpiar la cuenta

```
azure group delete --name $RESOURCE_GROUP 
``` 
 
 
 
