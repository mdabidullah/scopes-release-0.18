using import spicetools

spice test (x y z args...)
    assert ((x as i32) == 1)
    assert ((y as i32) == 2)
    assert ((z as i32) == 3)
    assert (('argcount args...) == 3)
    let u v w =
        'getarg args... 0
        'getarg args... 1
        'getarg args... 2
    assert ((u as i32) == 4)
    assert ((v as i32) == 5)
    assert ((w as i32) == 6)
    `(+ x y z u v w)

spice test-match (args...)
    # non-constant type
    let T = ('typeof `5.0)
    let i32T = ('typeof `5)
    spice-match args...
    case (a : T, b : T)
        `(print "case0" *...)
    case (a as i32, b as i32, c...)
        # both a and b must be i32 constants and will be unwrapped as such.
        # we can precompute a + b here
        `(print "case1" a b [(a + b)] "|" c...)
    case (a : i32, b : i32, c...)
        # a and b must implicitly cast to i32, and the cast will be
          auto-generated.
        `(print "case2" a b (a + b) "|" c...)
    case (x y z)
        # three arguments only
        `(print "case3" x y z)
    case (x y...)
        # at least one argument
        `(print "case4" x "|" y...)
    default
        error "wrong!"

@@ spice-quote
fn test-main ()
    test-match 1 2 3 4          # case 1
    test-match (4 - 3) 2 3 4    # case 2
    test-match 1:i8 2 3 4       # case 2
    test-match "a" "b" "c"      # case 3
    test-match "a" "b"          # case 4
    test-match 3.5 4

    assert
        (test 1 2 3 4 5 6) == 21

run-stage;

test-main;

return;