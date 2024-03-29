# Object Storage Exporter

This component provide usefull informations about each bucket of the storage object such as the size the last modification date etc...

## Exporter

Metrics :

- MONITORING_FINOPS_objects_size_sum_bytes{bucket="..."}
- MONITORING_FINOPS_objects_total{bucket="..."}
- MONITORING_FINOPS_biggest_object_size_bytes{bucket="..."}
- MONITORING_FINOPS_last_modified_object_date{bucket="..."}
- MONITORING_FINOPS_last_modified_object_size_bytes{bucket="..."}

## Parameters of the Chart

| Name | Description | Value |
| ---- | ----------- | ----- |
| `replicaCount` | Number of pods on the deployment | `1` |
| `image.registry` | Image registry | `""` |
| `image.repository` | Image repository | `""` |
| `image.tag` | Image tag (immutable tags are recommended) | `latest` |
| `image.pullPolicy` | Image pull policy | `Always` |
| `imagePullSecrets` | Specify image pull secrets | `[]` |
| `nameOverride` | Override the autogenerated name | `""` |
| `fullnameOverride` | Override the autogenerated fullname  | `""` |
| `internalPort` | Metrics endpoint port number on the pod | `2112` |
| `env` | Env values added to the pod | `{}` |
| `envFromSecret` | Env values added to the pod from secrets | `{}` |
| `service.type` | Type of the service | `ClusterIP` |
| `service.port` | Port of the service | `2112` |
| `rbac.create` | Create specific rbac & acoount for the exporter | `false` |
| `resources` | Pod resource requests & limits | `{}` |
| `nodeSelector` | Node labels for pod assignment | `[]` |
| `tolerations` | List of node taints to tolerate | `[]` |
| `affinity` | Node/pod affinities | `[]` |
| `metrics.serviceMonitor.interval` | Interval at which metrics should be scraped. | `10s` |
| `metrics.serviceMonitor.additionalLabels` | Additional labels that can be used so ServiceMonitor will be discovered by Prometheus | `{}` |
| `configs` | Env values added to the pod from configmaps | `{}` |
