![Pokemon Logo](http://vignette1.wikia.nocookie.net/es.pokemon/images/6/61/Logo_de_Pok%C3%A9mon_(EN).png)

# Instalar las cli

```
npm install -g azure-cli
```

# Crear un clúster

* **EDITA azuredeploy.parameters.json**

```bash
azure login
azure config mode arm
azure account list
azure account set 4c242bba-XXXX-XXXX-XXXX-464d90b6ef40
azure location list
``` 

```bash
set ADMIN_USERNAME=XXXXXXX
set RESOURCE_GROUP=XXXXXXX
set DEPLOYMENT_NAME=dcospokemon
set LOCATION=westeurope
set TEMPLATE_URI=https://raw.githubusercontent.com/Azure/azure-quickstart-templates/master/101-acs-dcos/azuredeploy.json
set PARAMFILE=azuredeploy.parameters.json

azure group create -n %RESOURCE_GROUP% -l %LOCATION% --template-uri %TEMPLATE_URI% -e %PARAMFILE% --name %DEPLOYMENT_NAME%

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

* Instalar jq: https://stedolan.github.io/jq
* Jugar con jqplay.org
* Revisar vms-scale-in-or-out.json

```
azure resource list %RESOURCE_GROUP% --resource-type Microsoft.Compute/virtualMachineScaleSets --json | jq .[].name
``` 

* Repetir el cambio de tamaño para el grupo privado y público

```
SET VMSS_TEMPLATE=https://raw.githubusercontent.com/gbowerman/azure-myriad/master/vmss-scale-in-or-out.json
azure group deployment create --resource-group %RESOURCE_GROUP% --template-uri %VMSS_TEMPLATE%
```

# Desplegar aplicación

* Instalar prettyjson

``` 
npm install -g prettyjson
```

* Explicar la aplicación
* Revisar conceptos de Docker
* Revisar deploy-pokemon.json
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
curl -X PUT -d "{ \"instances\": 3 }" -H "Content-type: application/json" http://localhost/marathon/v2/apps/azureday/v7
```

# Limpiar la cuenta

```
azure group deployment delete --resource-group %RESOURCE_GROUP% --name DEPLOYMENT_NAME
``` 
 
 
 
