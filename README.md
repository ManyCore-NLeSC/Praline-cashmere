# Praline-cashmere

Java server to compute sequence alignments using Cashmere and MCL.

To run on DAS-5:

Clone the repository `https://github.com/ManyCore-NLeSC/whole-genome-tool`.  This
contains the Python client code that preprocesses the sequences to annotate
them with motifs.

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
