# sample-azure-k8n
## Setting up an AKS cluster on Azure
Execute the following in the Azure console:
```bash
az aks create --name foo --resource-group azurus --kubernetes-version 1.9.2 
az provider register --namespace Microsoft.ContainerService
az aks get-credentials --resource-group=azurus --name=foo
```

##### Notes
1. `az aks create` takes a *long* time.  Be patient.
1. `az aks create` might fail with a misleading error message if because of capacity issues in your default region (50% success rate at the time of writing).  If this happens, there's nothing for it but to choose another region.  The error message gives incorrect information about which regions you can use.  You'll have to Google (or Bing, I suppose).
1. The `az provider register` foo is something some Microsoft people mentioned in an issue report.  I've no idea why you need to do it.
1. `az aks get-credentials` returns immediately, but takes an unspecified amount of time to actually work.  Be patient.
1. If it's all working, `kubectl get nodes` will show you the nodes you've created.

## Deploying to a cluster
To make life easy for you, Azure requires you to execute several commands to let your cluster see your container registry.

First, find out what the 'SPN' (Service Principal *P*rofile :-P) is for your new cluster:
```bash
$ az aks show -g azurus --name foo -o json | jq -r ".servicePrincipalProfile.clientId"
85ba59ba-1622-40df-b7ae-60ac492ea8f5
$
```

With that information, you might be able to conjure up some permissions for your cluster to access your container registry with this command, although it failed cryptically for me:
```bash
az role assignment create --role Contributor --assignee-object-id 85ba59ba-1622-40df-b7ae-60ac492ea8f5 --scope /subscriptions/c97858e6-3417-4146-940d-e0d6cd7d7db7/resourceGroups/azurus/providers/Microsoft.ContainerRegistry/registries/azurus
```

As a fall-back, you can hold your nose and poke about in the Azure UI to add your SPN as a 'Contributor' to the container registry.

Until I've been through the process of setting up CD, you can run a common or garden:
```bash
kubectl apply -f deployment.yaml
```

## Tearing down an AKS cluster on Azure
To avoid burning money, you'll probably want to tear down clusters when you're finished with them:
 ```bash
az aks delete --name foo --resource-group azurus
```
This also takes a *long* time.  Be patient.