
using import testing

do
    let x = (_ 1 2 3)
    let y = (_ 4 5 6)
    let z = (_ 7 8 9)

    test (x == 1)
    test (y == 4)
    test (z == 7)

do
    let x y z = 1 2 (_ 3 4 5 6 7 8 9)

    test (x == 1)
    test (y == 2)
    test (z == 3)

do
    # let returns all arguments it declared
    let x y z =
        let u v w =
            _ 1 2 3

    test (x == 1)
    test (y == 2)
    test (z == 3)
    test (u == 1)
    test (v == 2)
    test (w == 3)

do
    fn ret2 (x y)
        _ x y

    let k0 k1 k2 k3 =
        ret2 1 2
        ret2 3 4
        ret2 5 6
        ret2 7 8

    test ((va-countof k0) == 1)
    test ((va-countof k1) == 1)
    test ((va-countof k2) == 1)
    test ((va-countof k3) == 1)

do
    # if condition block scope is accessible in subsequent blocks
    fn get () (_ true 303)
    if (let ok n = (get))
        test (n == 303)
    else
        error "failed"

    test (n == 303)

let somefunc =
    fn () true

dump somefunc

fn unconst (x) x

do
    let scope =
        do
            let x = true
            locals;
    static-assert scope.x

# ASSERT OK: no attribute 'z in scope
test-compiler-error
    do
        let scope =
            do
                let x y z w = 5 6 7 8
                unlet x y z
                locals;
        scope.z

# ASSERT OK: no attribute 'x in scope
test-error
    do
        let scope =
            do
                let x y z w = (unconst 5) (unconst 6) (unconst 7) (unconst 8)
                unlet x y z
                locals;
        '@ scope 'z

# ASSERT OK: excess argument is not bound to a name
test-compiler-error
    do
        let a = (1 + 2) + 3
