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
	"os"
	"time"
)

type header struct {
	Mtype      string `json:"type"`
	Timesptamp string `json:"timesptamp"`
	Level      string `json:"level"`
	Line       string `json:"line,omitempty"`
	File       string `json:"file,omitempty"`
	Thread     string `json:"thread,omitempty"`
}

type message struct {
	Content string `json:"content"`
}

type log struct {
	Header  header  `json:"header"`
	Message message `json:"message"`
}

func newLog(level string, content string) log {
	t := time.Now()
	header := header{
		Mtype:      "LOG",
		Timesptamp: fmt.Sprintf("%04d-%02d-%02dT%02d:%02d:%02d.%06dZ", t.Year(), t.Month(), t.Day(), t.Hour(), t.Minute(), t.Second(), t.Nanosecond()/1000),
		Level:      level,
	}
	message := message{
		content,
	}
	return log{header, message}
}

func (l log) toString() string {
	strb, err := json.Marshal(&l)
	if err != nil {
		DisplayLog("ERROR", fmt.Sprintf("Failed to marshal log %v", l))
	}
	return string(strb)
}

func DisplayLog(level string, content string) {
	log := newLog(level, content)
	if level == "ERROR" || level == "FATAL" || level == "WARNING" {
		fmt.Fprintln(os.Stderr, log.toString())
	} else if level == "INFO" || level == "DEBUG" {
		fmt.Fprintln(os.Stdout, log.toString())
	} else {
		DisplayLog("ERROR", "The loglevel "+level+" does not exists!")
	}
}
