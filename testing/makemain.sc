
using import testing

# generate a new main executable

bin-dir := compiler-dir .. "/bin"
include-dir := compiler-dir .. "/include"

let lib =
    include
        options "-I" include-dir
        """"#include "scopes/scopes.h"

            int system(const char *command);

using lib.extern

#-----------------

fn get-executable-ptr ()
    bitcast (static-typify this-function) voidstar

fn main (argc argv)
    sc_init (get-executable-ptr) argc argv
    return (sc_main)

#-----------------

objfile := bin-dir .. "/newscopes.o"
compile-object
    default-target-triple
    compiler-file-kind-object
    objfile
    do
        let main =
            static-typify main i32 (mutable (@ (mutable (@ i8))))
        locals;

# build new binary
binfile := bin-dir .. "/newscopes"
let cmd =
    .. "gcc"
        \ " " objfile
        \ " -o " binfile
        \ " -Wl,-rpath=\\$ORIGIN"
        " -lscopesrt"
        \ " -L " bin-dir
print cmd
test ((system cmd) == 0)

# and execute it
print "launching" binfile
test ((system binfile) == 0)


