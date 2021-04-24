#!/bin/bash

java -jar $(ls -tr ./bin/*.jar | head -1)