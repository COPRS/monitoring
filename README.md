:arrow_heading_up: Go back to the [Reference System Sotfware repository](https://github.com/COPRS/reference-system-software) :arrow_heading_up:

# Monitoring

This repository contains [RS-CORE Monitoring](#rs-core-monitoring) to handle trace to be able to monitor state of product produced by ingestion chain and even all Sentinel processing chain.

The repository also contains two FINOPS components:

- [object-storage-exporter](#object-storage-exporter)
- [resources-exporter](#resources-exporter)
- [Grafana plugins](#grafana-plugin)

## RS-CORE Monitoring

[![RS-CORE Monitoring](https://github.com/COPRS/monitoring/actions/workflows/docker-ci-traceprocessor.yml/badge.svg)](https://github.com/COPRS/monitoring/actions/workflows/docker-ci-traceprocessor.yml)

**RS-CORE Monitoring** process trace by filtering desired one and ingesting them in database.
![trace processor workflow](trace-processor/inputs/trace-processor_workflow.png)

The description of each component of RS-Core is described [here](./trace-processor/README.md)

### Installation

#### Prerequisites

- Infrastructure : all the required tools (such as Kafka and PostgreSQL) are included in the RS infrastructure installation.  
  See  [Reference System Software Infrastructure](https://github.com/COPRS/infrastructure) for details.
- In PostgreSQL, you have to create an empty database (for example: **monitoring**) with a user (and his password) having read/write access to it. This information will be updated in [RS-CORE configuration files](./rs-cores/MONITORING/Release_Note.md)

Note : The rs-core monitoring will try to deploy itself in the monitoring namespace by default. However, this is only possible if you configured SCDF to use different namespace in stream deployments : <https://github.com/COPRS/infrastructure/blob/main/docs/user_manuals/how-to/RS%20Add-on%20-%20RS%20Core.md#use-different-namespaces-in-stream-deployments>. If you wish to change the default behavior and deploy the component in another namespace, edit the `namespace` property in the following file :

- rs-cores/MONITORING/Executables/additional_resources/ConfigMap-filter.yaml
- rs-cores/MONITORING/Executables/additional_resources/ConfigMap-ingestor.yaml
- rs-cores/MONITORING/Executables/additional_resources/Secret-ingestor.yaml

#### Build

In order to build the project from source, first clone the GitHub repository :

```shellsession
git clone https://github.com/COPRS/monitoring.git
```

Then build the docker images:

```shellsession
mvn clean deploy -Djib.dest-registry=local/rs-docker/monitoring -Djib.goal=dockerBuild
```

And finally build the zip files:

```shellsession
./rs-cores/build_cores.sh
```

The zip files will be found in the rs-core folder.

#### Using Ansible

Run the `deploy-rs-addon.yaml` playbook with the following variables:

- **stream_name**: name given to the stream in *Spring Cloud Dataflow*
- **rs_addon_location**: direct download url of the zip file or zip location on the bastion

#### Manual Install

Download and extract the zip file for the RS-Core to install.  
If necessary, edit the parameters as required (See [Release Note](./rs-cores/MONITORING/Release_Note.md) for parameters description).

- Create all objects defined by files in *Executables/additional_resources*
- Using the SCDF GUI:
  - Register the applications using the content of the *stream-application-list.properties* file
  - Create the streams using the content fo the *stream-definition.properties* file
  - Deploy the stream using the properties defined in the *stream-parameters.properties* file (removing comments)

### Uninstall

Using the SCDF GUI, undeploy then destroy the stream relative to the RS-Core.

## FINOPS components

[![Docker CI FINOPS](https://github.com/COPRS/monitoring/actions/workflows/docker-ci-finops.yml/badge.svg)](https://github.com/COPRS/monitoring/actions/workflows/docker-ci-finops.yml)

[![Helm FINOPS](https://github.com/COPRS/monitoring/actions/workflows/helm-finops.yml/badge.svg)](https://github.com/COPRS/monitoring/actions/workflows/helm-finops.yml)

These components provide metrics about Cloud Providers resources.

### Object Storage Exporter

This component provide metrics on real time use of storage (object storage)

#### Installation

##### Prerequisites

- Infrastructure : all the required tools (such as Prometheus) are included in the RS infrastructure installation.  
  See  [Reference System Software Infrastructure](https://github.com/COPRS/infrastructure) for details.

##### Build

In order to build the project from source, first clone the GitHub repository :

```shellsession
git clone https://github.com/COPRS/monitoring.git
```

Then build the docker images:

```shellsession
docker build -t <name_image>:<tag> ./finops/object-storage-exporter
```

##### Installing the Chart

To install the chart with the release name `my-release`:

```shellsession
helm repo add rs-artifactory-monitoring https://artifactory.coprs.esa-copernicus.eu/artifactory/rs-helm
helm install my-release rs-artifactory-monitoring/finops-object-storage-exporter
```

These commands deploy Object Storage Exporter on the Kubernetes cluster in the default configuration. [Parameters](./finops/object-storage-exporter/README.md#parameters-of-the-chart) that can be configured during installation.

> Tip: List all releases using `helm list`

### Uninstalling the Chart

To uninstall/delete the `my-release` resources:

```shellsession
helm delete my-release
```

The command removes all the Kubernetes components associated with the chart and deletes the release. Use the option `--purge` to delete all history too.

### Resources Exporter

This component provide metrics on real time use of resources (machines, managed services)

#### Installation

##### Prerequisites

- Infrastructure : all the required tools (such as Prometheus) are included in the RS infrastructure installation.  
  See  [Reference System Software Infrastructure](https://github.com/COPRS/infrastructure) for details.

##### Build

In order to build the project from source, first clone the GitHub repository :

```shellsession
git clone https://github.com/COPRS/monitoring.git
```

Then build the docker images:

```shellsession
docker build -t <name_image>:<tag> ./finops/resources-exporter
```

##### Installing the Chart

To install the chart with the release name `my-release`:

```shellsession
helm repo add rs-artifactory-monitoring https://artifactory.coprs.esa-copernicus.eu/artifactory/rs-helm
helm install my-release rs-artifactory-monitoring/finops-resources-exporter
```

These commands deploy Resources Exporter on the Kubernetes cluster in the default configuration. [Parameters](./finops/resources-exporter/README.md#parameters-of-the-chart) that can be configured during installation.

> Tip: List all releases using `helm list`

### Uninstalling the Chart

To uninstall/delete the `my-release` resources:

```shellsession
helm delete my-release
```

The command removes all the Kubernetes components associated with the chart and deletes the release. Use the option `--purge` to delete all history too.

## Grafana plugin

[![Yarn CI for Grafana plugins](https://github.com/COPRS/monitoring/actions/workflows/yarn-grafana-plugins.yml/badge.svg)](https://github.com/COPRS/monitoring/actions/workflows/yarn-grafana-plugins.yml)
### invalidations-completeness

This plugin is a panel used to manage invalidations associated to missing products.

### invalidations-timeliness

This plugin is a panel used to manage invalidations associated to late products.

### Local build

#### Requirements

- Node.js = 17.3
- Yarn = 1.22.19

#### Build

```shellsession
cd grafana-monitoring/grafana/plugins/<plugin_name>
yarn install && yarn build && rm -rf node_modules
```

### Installation

Update the default configuration of the grafana apps [following the infrastructure guide](https://github.com/COPRS/infrastructure/tree/main#review-and-change-the-default-configuration-to-match-your-needs) and add the two plugins :

```yaml
custom_plugins:
  - name: cs-group-invalidations-completeness
    url: https://artifactory.coprs.esa-copernicus.eu/artifactory/rs-zip/monitoring/invalidations-completeness.zip
  - name: cs-group-invalidations-timeliness
    url: https://artifactory.coprs.esa-copernicus.eu/artifactory/rs-zip/monitoring/invalidations-timeliness.zip
```

## Repository Content

The artifactory repository should contain:

- Docker images for all components : [https://artifactory.coprs.esa-copernicus.eu/ui/repos/tree/General/rs-docker/monitoring](https://artifactory.coprs.esa-copernicus.eu/ui/repos/tree/General/rs-docker/monitoring)
- RS-Core Monitoring : [https://artifactory.coprs.esa-copernicus.eu/ui/repos/tree/General/rs-zip](https://artifactory.coprs.esa-copernicus.eu/ui/repos/tree/General/rs-zip)
- Helm Chart for FINOPS components : [https://artifactory.coprs.esa-copernicus.eu/ui/repos/tree/General/rs-helm/monitoring](https://artifactory.coprs.esa-copernicus.eu/ui/repos/tree/General/rs-helm/monitoring)
- Grafana plugins : [https://artifactory.coprs.esa-copernicus.eu/artifactory/rs-zip-local/monitoring](https://artifactory.coprs.esa-copernicus.eu/artifactory/rs-zip-local/monitoring)

---
# Copyright and license

The Reference System Software as a whole is distributed under the Apache License, version 2.0. A copy of this license is available in the [LICENSE](LICENSE) file. Reference System Software depends on third-party components and code snippets released under their own license (obviously, all compatible with the one of the Reference System Software). These dependencies are listed in the [NOTICE](NOTICE.md) file.

<p align="center">
 <img src="/docs/media/banner.jpg" width="800" height="50" />
</p>
<p align="center">This project is funded by the EU and ESA.</p>
