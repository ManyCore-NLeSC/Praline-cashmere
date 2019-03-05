# Praline-cashmere

## Introduction

Praline-Cashmere is computes sequence alignments using Cashmere and MCL.  This
document describes how to install it on the [DAS-5](http://www.cs.vu.nl/das5)
and how to install it locally.

## DAS-5 Installation

### Prerequisites

#### Install `miniconda`

Since the DAS-5 has a Python implementation that is too old, we use the Conda
package manager to install a new Python.  Download the `bash` 64-bits installer
from <https://conda.io/miniconda.html> and run it on the DAS-5.  Accept the
license and choose a convenient path for the installation.  We ignore the
warning for the `PYTHONPATH`; in this case it is something that was set by the
CUDA package.  We let the installer change the PATH in `~/.bashrc` and we have
to source this file to let the change have effect.

#### Install needed python packages

We need the following python packages:

```bash
conda install numpy
```

#### Install `praline`

```bash
git clone praline.git
cd praline
```

Then install Praline from the `praline` directory:

```bash
python setup.py install
```

#### Install `MA-PRALINE`

This repository contains the motif-aware part of praline.

```bash
git clone MA-PRALINE.git
cd MA-PRALINE
```

In `setup.py` we remove the dependency on `praline` by removing package
`PRALINE>=1.0` from the variable `install_requires`.

Then we can run:

```bash
python setup.py install
```

#### Install `whole-genome-tool`

Clone the repository `https://github.com/ManyCore-NLeSC/whole-genome-tool`.  This
contains the Python client code that preprocesses the sequences to annotate
them with motifs.



### Installation of Praline-cashmere

Clone this repository and run Gradle in the repository, which will create the
installation directory:

```bash
./gradlew installDist
```

Export the following two variables:

```bash
export PRALINE_CASHMERE_DIR=/path/to/Praline-cashmere/build/install/praline-cashmere
export CASHMERE_PORT=<choose your own port>
```

### Running Praline Cashmere

Cashmere uses a server on the headnode to coordinate the compute nodes.  In a
separate terminal, to start the server, go to the
directory `$PRALINE_CASHMERE_DIR/bin` and run:

```bash
./cashmere-server
```

Then from the original terminal, run (again in $PRALINE_CASHMERE_DIR/bin) for two TitanX nodes:
```bash
./praline-cashmere TitanX=2
```

To run it with other node options, just run `praline-cashmere` without arguments.

This will create a file called `bowbeforeme` in `$PRALINE_CASHMERE_DIR` which lists
the hostname of the server node, which is read by the python script.

In the `whole-genome-tool` directory, run: 

```bash
python wgt.py input/test.json
```
(or other file describing job instead of test.json)

To disable the cluster, set `use_our_stuff` to `False` in `manager.py`.	


## Local installation

The standard Python on the DAS-5 is version 2 whereas on a more up-to-date
machine, it is likely to be Python 3.  Ultimately, we are going to use the
`whole-genome-tool` library to drive the computation that is written in Python
2, so we will install all the Python packages with Python 2.

### Prerequisites

#### Install needed python packages

We need the following Python (2) packages:

- `numpy`
- `requests`

#### Install `praline`

```bash
git clone praline.git
cd praline
```

Then install Praline from the `praline` directory:

```bash
python2 setup.py install --user
```

#### Install `MA-PRALINE`

This repository contains the motif-aware part of praline.

```bash
git clone MA-PRALINE.git
cd MA-PRALINE
```

In `setup.py` we remove the dependency on `praline` by removing package
`PRALINE>=1.0` from the variable `install_requires`.

Then we can run:

```bash
python2 setup.py install --user
```

#### Install `whole-genome-tool`

Clone the repository `https://github.com/ManyCore-NLeSC/whole-genome-tool`.  This
contains the Python client code that preprocesses the sequences to annotate
them with motifs.

### Installation of Praline-cashmere

Clone this repository and run Gradle in the repository, which will create the
installation directory:

```bash
./gradlew installDist
```

Export the following two variables:

```bash
export PRALINE_CASHMERE_DIR=/path/to/Praline-cashmere/build/install/praline-cashmere
export CASHMERE_PORT=<choose your own port>
```

### Running Praline Cashmere

Go to the directory `$PRALINE_CASHMERE_DIR/bin` and run:

```bash
./praline-cashmere.local
```
This will create a file called `bowbeforeme` in `$PRALINE_CASHMERE_DIR` which lists
the hostname of the server node, which is read by the python script.

In the `whole-genome-tool` directory, run: 

```bash
python2 wgt.py input/test.json
```
(or other file describing job instead of test.json)


