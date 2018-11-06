# Praline-cashmere
Java server to compute sequence alignments using Cashmere and MCL.

To run on DAS-5:

clone this repository
clone the repository @ https://github.com/ManyCore-NLeSC/whole-genome-tool, this contains the python client code

run gradlew jar here, this will create the build directory
export an evironment variable PRALINE_CASHMERE_DIR and set it to build/install/praline-cashmere
export CASHMERE_PORT=8265

in the directory build/install/praline-cashmere/bin
run cashmere-server
run praline-cashmere TitanX=2
for 2 titanX nodes, run praline-cashmere without arguments to get more node options

This will create a file called bowbeforeme in your home directory which lists the hostname of the server node, 
which is read by the python script. 

in the whole-genome-tool directory, run 

python wgt.py input/test.json 
(or other file describing job instead of test.json)

in manager.py set use_our_stuff to False to disable use of cluster.
