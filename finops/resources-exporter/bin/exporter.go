// Copyright 2023 CS Group
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package main

import (
	"flag"
	"fmt"
	"net/http"
	"os"
	"strconv"
	"time"

	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promhttp"
)

var (
	isDebug              bool
	apiURL               string
	queueName            string
	port                 int
	endpoint             string
	registry             *prometheus.Registry
	machineGabaritGauges = prometheus.NewGaugeVec(
		prometheus.GaugeOpts{
			Namespace: "MONITORING",
			Subsystem: "FINOPS",
			Name:      "machineUsage",
			Help:      "Infos about all the machines currently in the k8s cluster",
		},
		[]string{
			"name",
			"cpu",
			"memory",
		},
	)
)

type processingTypes []string

func watchMachineGabarit() {
	for {
		nodeGabarits, err := GetNodeGabarits()
		if err != nil {
			DisplayLog("FATAL", err.Error())
			os.Exit(1)
		}
		machineRepartition, err := MatchAll(nodeGabarits)
		if err != nil {
			DisplayLog("FATAL", err.Error())
			os.Exit(2)
		}
		for template, nbMachine := range machineRepartition {
			machineGabaritGauges.WithLabelValues(
				template.Name,
				strconv.Itoa(template.CPU),
				strconv.Itoa(template.Memory),
			).Set(float64(nbMachine))
		}
		time.Sleep(5 * time.Second)
	}
}

func init() {
	const (
		isDebugDefault  = false
		isDebugUsage    = "Run in debug mode"
		portDefault     = 2112
		portUsage       = "Port to export on"
		endpointDefault = "/metrics"
		endpointUsage   = "Endpoint to export on"
	)
	flag.BoolVar(&isDebug, "debug", isDebugDefault, isDebugUsage)
	flag.BoolVar(&isDebug, "d", isDebugDefault, isDebugUsage+" (shorthand)")
	flag.IntVar(&port, "port", portDefault, portUsage)
	flag.IntVar(&port, "p", portDefault, portUsage+" (shorthand)")
	flag.StringVar(&endpoint, "endpoint", endpointDefault, endpointUsage)
	flag.StringVar(&endpoint, "e", endpointDefault, endpointUsage+" (shorthand)")

	if boolValue, err := strconv.ParseBool(os.Getenv("API_EXPORTER_DEBUG")); err == nil {
		isDebug = boolValue
	}

	prometheus.MustRegister(machineGabaritGauges)
}

func main() {
	flag.Parse()

	go watchMachineGabarit()

	DisplayLog("INFO", fmt.Sprintf("machine-usage-exporter started to listen on http://x.x.x.x:%d%s", port, endpoint))
	http.Handle(endpoint, promhttp.Handler())
	http.ListenAndServe(":"+strconv.Itoa(port), nil)
	DisplayLog("INFO", "Machine-usage-exporter stopped")
}
