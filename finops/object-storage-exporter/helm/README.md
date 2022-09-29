# Configuration

Parameter | Description | Default
--- | --- | ---
`replicaCount` | Number of pods on the deployment | `1`
`image.registry` | Image registry | ``
`image.repository` | Image repository | ``
`image.tag` | ImageTag | `"latest"`
`image.pullPolicy` | Image pull policy | `"Always"`
`imagePullSecrets` | Specify image pull secrets | `[]`
`nameOverride` | Override the autogenerated name | ``
`fullnameOverride` | Override the autogenerated fullname  | ``
`internalPort` | Metrics endpoint port number on the pod | `2112`
`env` | Env values added to the pod | `{}`
`envFromSecret` | Env values added to the pod from secrets | `{}`
`service.type` | Type of the service | `"ClusterIP"`
`service.port` | Port of the service | `"2112"`
`rbac.create` | Create specific rbac & acoount for the exporter | `false`
`resources` | Pod resource requests & limits | `{}`
`nodeSelector` | Node labels for pod assignment | `[]`
`tolerations` | List of node taints to tolerate | `[]`
`affinity` | Node/pod affinities | `[]`
`metrics.serviceMonitor.additionalLabels` | Additional labels that can be used so ServiceMonitor will be discovered by Prometheus | `{}`