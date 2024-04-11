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
	"encoding/json"
	"fmt"
	"io/ioutil"
	"math"
	"os"
	"path/filepath"
)

var (
	templates Templates
)

// Templates todo
type Templates struct {
	Templates []Template `json:"templates"`
}

// Template todo
type Template struct {
	Name   string `json:"name"`
	CPU    int    `json:"cpu"`
	Memory int    `json:"memory"`
}

func loadTemplate() error {
	filePath := filepath.Join(os.Getenv("HOME"), ".config", "exporter", "templates.json")
	if os.Getenv("TEMPLATES_FILE") != "" {
		filePath = os.Getenv("TEMPLATES_FILE")
	}
	data, err := ioutil.ReadFile(filePath)
	if err != nil {
		return fmt.Errorf("The templates cannot be read - %s", err.Error())
	}
	if err := json.Unmarshal(data, &templates); err != nil {
		return fmt.Errorf("The templates cannot be unmarshalled - %s", err.Error())
	}
	return nil
}

func match(gabarit NodeGabarit) *Template {
	if len(templates.Templates) == 0 {
		loadTemplate()
	}
	for i := range templates.Templates {
		if templates.Templates[i].CPU == gabarit.CPU && float64(templates.Templates[i].Memory) == math.Ceil(float64(gabarit.Memory)/1024/1024/1024) {
			return &templates.Templates[i]
		}
	}
	DisplayLog("WARNING", fmt.Sprintf("No template matching the NodeGabarit %s", gabarit.Name))
	return nil
}

// MatchAll todo
func MatchAll(gabarits []NodeGabarit) (map[*Template]int, error) {
	repartition := make(map[*Template]int)
	if len(templates.Templates) == 0 {
		if err := loadTemplate(); err != nil {
			return repartition, fmt.Errorf("Failed to load the templates - %s", err.Error())
		}
	}
	for _, gabarit := range gabarits {
		if gabarit.Readiness != "Unknown" {
			if template := match(gabarit); template != nil {
				repartition[template]++
			}
		}
	}

	return repartition, nil
}
