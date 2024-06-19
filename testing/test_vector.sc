
using import testing

let mask = (sugar-eval (vectorof i32 7 5 3 1))

fn do-ops (v w)
    v + w * w

fn test-vector-ops ()
    let v = (vectorof i32 10 20 (unpack (vectorof i32 30 40)))
    let w = (vectorof i32 1 2 3 4)
    test
        all?
            (do-ops v w) == (vectorof i32 11 24 39 56)
    test
        all?
            (shufflevector v w mask) == (vectorof i32 4 2 40 20)

let VT = (vector i32 4)

# working with variables
test-vector-ops;

test ((length (vectorof f32 2 10 11)) == 15.0)

print
    lslice (vectorof i32 0 1 2 3 4 5 6 7) 3
print
    rslice (vectorof i32 0 1 2 3 4 5 6 7) 4

test
    all?
        ==
            ? true
                vectorof i32 1 2 3 4
                vectorof i32 5 6 7 8
            vectorof i32 1 2 3 4

test
    all?
        ==
            ? (vectorof bool true false true false)
                vectorof i16 1 0 3 0
                vectorof i16 0 2 0 4
            vectorof i16 1 2 3 4

# reduce on big vector sizes
test ((vector-reduce add (vectorof i32 1 2 3 4 5 6 7 8 9 10 11 12)) == 78)

test (constant? (vectorof i32 11 24.5 39 56))

test
    all?
        ==
            bitcast (vectorof i8 1 2 3 4 5 6 7 8) (vector i16 4)
            vectorof i16 0x0201 0x0403 0x0605 0x0807

test
    ==
        bitcast (vectorof i8 1 2 3 4) u32
        0x04030201

test
    all?
        ==
            bitcast 0x04030201 (vector i8 4)
            (vectorof i8 1 2 3 4)


# casting between bit vectors and integers
let V = (vectorof bool true true false false true false true false)
test ((bitcast V u8) == 0b1010011)
test (all? ((bitcast 0b1010011:u8 (vector bool 8)) == V))

;