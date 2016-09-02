# Prerequisitos

* Necesitarás las *cli* para interactuar con Azure. Instala [nodejs](https://nodejs.org/en/) previamente y a continuación  ```npm install -g azure-cli```
* También necesitarás *ssh* en tu sistema. Si utilizas Windows la forma más sencilla de tenerlo es instalando [git for Windows](https://git-scm.com/download/win).
* También tienes que tener una pareja de claves RSA. En Windows **y solo si no tienes previamente clave generada**: ```ssh-keygen -t rsa -b 2048 -C "email@dominio.com"``` y contesta *enter* a todo.
 
# Crear un clúster

* **EDITA azuredeploy.parameters.json** modificando los parámetros correspondientes.

```bash
azure login
azure config mode arm
azure account list
azure account set 4c242bba-XXXX-XXXX-XXXX-464d90b6ef40
azure location list
azure provider register --namespace Microsoft.ContainerService
``` 

```bash
set ADMIN_USERNAME=<tu_username>
set RESOURCE_GROUP=<un_nombre_lógico>
set ACS_NAME=containerservice-%RESOURCE_GROUP%
set LOCATION=westeurope
set TEMPLATE_URI=https://raw.githubusercontent.com/Azure/azure-quickstart-templates/master/101-acs-dcos/azuredeploy.json
set PARAMFILE=azuredeploy.parameters.json

cd azure-arm
azure group create -n %RESOURCE_GROUP% -l %LOCATION% --template-uri %TEMPLATE_URI% -e %PARAMFILE%

azure group deployment show %RESOURCE_GROUP% azuredeploy | grep State
```

# Gestionar clúster mediante web

```
set MASTER=%RESOURCE_GROUP%mgmt.westeurope.cloudapp.azure.com
set AGENTS=%RESOURCE_GROUP%agents.westeurope.cloudapp.azure.com
start ssh -L 80:localhost:80 -N %ADMIN_USERNAME%@%MASTER% -p 2200 
start http://localhost:80
```

# Gestionar Mesos

```
start http://localhost:80/mesos
```

# Administrar master node

Visualizar cómo la IP pública del máster coincide con la que muestra el panel web.

```
ssh %ADMIN_USERNAME%@%MASTER% -p 2200
ifconfig | grep "inet addr"
```

# Redefinir el número de instancias en vmss público

* Revisar vms-scale-in-or-out.json

```
azure resource list %RESOURCE_GROUP% --resource-type Microsoft.Compute/virtualMachineScaleSets --json  
``` 

* Apunta los nombres de los vmss públicos y privados (propiedad *name*, por ejemplo "dcos-agent-public-2D554AAB-vmss0")
* Aplicar el siguiente paso tanto al grupo público (3 instancias) como al privado (1 instancia)

```
SET SCALE_TEMPLATE=https://raw.githubusercontent.com/gbowerman/azure-myriad/master/vmss-scale-in-or-out.json
azure group deployment create --resource-group %RESOURCE_GROUP% --template-uri %SCALE_TEMPLATE%
```

# Desplegar aplicación

* Instalar prettyjson con ```npm install -g prettyjson```
* Si estás con Windows, instalar [curl](https://curl.haxx.se/download.html)
* Explicar la aplicación
* Revisar conceptos de Docker
* Revisar [deploy-pokemon.json](https://github.com/capside/azure-mesos-pokemon/blob/master/azure-arm/deploy-pokemon.json)
* Ver el [API](https://mesosphere.github.io/marathon/docs/rest-api.html) de Marathon

```
curl -X POST http://localhost/marathon/v2/apps -d @deploy-pokemon.json -H "Content-type: application/json" | prettyjson
```

# Visualizar el número de contenedores desplegados

* Visualizar el estado en http://localhost/marathon

```
start http://%AGENTS%:8080
curl -s http://localhost/marathon/v2/apps | prettyjson | grep instances
```

# Modificar el número de contenedores

```
curl -X PUT -d "{ \"instances\": 3 }" -H "Content-type: application/json" http://localhost/marathon/v2/apps/poki
```

# Comprobar la resiliencia de los contenedores

* Recargar la aplicación ```start http://%AGENTS%:8080```
* Pulsar sobre uno de los Pokémon
* Visualizar cómo desaparece el contenedor ```start http://localhost/#/nodes/list/``` 
* En unos segundos reaparecerá un nuevo Pokémon 

# Limpiar la cuenta

```
azure group deployment delete --resource-group %RESOURCE_GROUP% --name azuredeploy
``` 
 
 
 