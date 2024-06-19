
# refcounting

using import testing
using import Rc
using import glm

do
    let a = (Rc.wrap (One 303))
    let b = (Rc.new One 303)

    'check a

    let k = ((Rc i32) 3)
    report k
    dump (qualifiersof k)

    do
        # Rc comparisons are object comparisons
        let m = ((Rc i32) 3)
        test (k == k)
        test (k != m)

    do
        test ((Rc.strong-count k) == 1)
        let k2 = (copy k)
        test ((Rc.strong-count k) == 2)
    test ((Rc.strong-count k) == 1)

    test (k * 2 == 6)

    k = 12
    test (k == 12)

    let q = ((Rc.view k) * 2)
    test (q == 24)

    dump (qualifiersof a) (qualifiersof b)

    let c = ((Rc vec3) 1 2 3)

    test ((Rc.strong-count c) == 1)
    test ((Rc.weak-count c) == 0)

    let nullweak = ((Weak vec3))
    test ((Rc.strong-count nullweak) == 0)
    test ((Rc.weak-count nullweak) == 1)
    let nullweak2 = ((Weak vec3))
    test ((Rc.weak-count nullweak) == 1)
    test ((Rc.weak-count nullweak2) == 1)
    test-error ('upgrade nullweak2)
    let nullweak3 = (copy nullweak)
    test (nullweak == nullweak2)
    test (nullweak == nullweak3)

    let w = (c as Weak)

    test ((Rc.strong-count c) == 1)
    test ((Rc.weak-count c) == 1)
    test ((Rc.strong-count w) == 1)
    test ((Rc.weak-count w) == 1)

    let v = ('force-upgrade w)
    let p = v

    test ((Rc.strong-count c) == 2)
    test ((Rc.weak-count c) == 1)
    test ((Rc.strong-count w) == 2)
    test ((Rc.weak-count w) == 1)
    test ((Rc.strong-count p) == 2)
    test ((Rc.weak-count p) == 1)

    let w2 = (copy w)
    test ((Rc.strong-count p) == 2)
    test ((Rc.weak-count p) == 2)
    test (w == w2)
    drop w2

    print c
    test (c.xz == (vec2 1 3))
    drop c
    drop v

    test ((Rc.strong-count w) == 0)
    test ((Rc.weak-count w) == 1)

    test-error ('upgrade w)

    ;

One.test-refcount-balanced;

# recursive declarations
do
    using import struct
    struct Node
        parent : (Rc this-type)

# using strong references to build a tree
    this tests if a complex tree of strong references is cleaned up properly
do
    using import struct
    using import Array

    global deleted_names : (GrowingArray string)

    struct DemoNode
        let RcType = (Rc this-type)
        children : (GrowingArray RcType)
        _name : string

        inline... new
        case (name : string,)
            RcType
                _name = name
        case (parent : RcType, name : string, )
            let self =
                RcType
                    _name = name
            'append parent.children (copy self)
            self

        inline __== (self other)
            if ((typeof self) == (typeof other))

        inline __drop (self)
            print "deleting" ('name self)
            'append deleted_names ('name self)
            super-type.__drop self

        inline name (self)
            self._name

        inline __repr (self)
            deref self._name

    let root = (DemoNode.new "root")
    do
        let n1 = (DemoNode.new root "n1")
        let n2 = (DemoNode.new root "n2")
        let n3 = (DemoNode.new root "n3")

        let n11 = (DemoNode.new n1 "n11")
        let n21 = (DemoNode.new n2 "n21")
        let n22 = (DemoNode.new n2 "n22")
        let n31 = (DemoNode.new n3 "n31")
        let n32 = (DemoNode.new n3 "n32")
        let n33 = (DemoNode.new n3 "n33")

        let n321 = (DemoNode.new n32 "n321")
        let n322 = (DemoNode.new n32 "n322")

        ;

    drop root
    print "ok"
    test ((countof deleted_names) == 12)
    test ((deleted_names @ 0) == "root")
    test ((deleted_names @ 1) == "n1")
    test ((deleted_names @ 2) == "n11")
    test ((deleted_names @ 3) == "n2")
    test ((deleted_names @ 4) == "n21")
    test ((deleted_names @ 5) == "n22")
    test ((deleted_names @ 6) == "n3")
    test ((deleted_names @ 7) == "n31")
    test ((deleted_names @ 8) == "n32")
    test ((deleted_names @ 9) == "n321")
    test ((deleted_names @ 10) == "n322")
    test ((deleted_names @ 11) == "n33")

# variation of same test to build a tree
    this tests if a complex tree of strong and weak references is updated correctly
do
    using import struct
    using import Array
    using import enum

    struct DemoNode
        let RcType = (Rc this-type)
        let WeakType = RcType.WeakType
        parent : WeakType
        children : (GrowingArray RcType)
        _name : string

        inline... new
        case (name : string,)
            RcType
                _name = name
        case (parent : RcType, name : string, )
            let self =
                RcType
                    parent = parent
                    _name = name
            'append parent.children (copy self)
            self

        inline __== (self other)
            if ((typeof self) == (typeof other))

        inline __drop (self)
            print "deleting" ('name self)
            super-type.__drop self

        inline name (self)
            self._name

        inline __repr (self)
            deref self._name

        fn child-index (self child)
            for i elem in (enumerate self.children)
                if (elem == child)
                    return i
            else
                assert false "unexpected error in child-index()"
                unreachable;

        fn unparent (self)
            let oldparent =
                try ('upgrade self.parent)
                else
                    return (copy self)
            let i = ('child-index oldparent self)
            self.parent = (WeakType)
            'remove oldparent.children i

        fn... reparent (self newparent)
            let self = (unparent self)
            self.parent = newparent
            'append newparent.children self

    let root = (DemoNode.new "root")
    do
        let n1 = (DemoNode.new root "n1")
        let n2 = (DemoNode.new root "n2")
        let n3 = (DemoNode.new root "n3")

        let n11 = (DemoNode.new n1 "n11")
        let n21 = (DemoNode.new n2 "n21")
        let n22 = (DemoNode.new n2 "n22")
        let n31 = (DemoNode.new n3 "n31")
        let n32 = (DemoNode.new n3 "n32")
        let n33 = (DemoNode.new n3 "n33")

        let n321 = (DemoNode.new n32 "n321")
        let n322 = (DemoNode.new n32 "n322")
        ;

    fn... print-tree (node, indent = "")
        returning void
        print
            indent .. ('name node)
            Rc.strong-count node
        test ((Rc.strong-count node) == 1)
        indent := indent .. "  "
        for child in node.children
            this-function child indent

    print-tree root
    do
        let node = (root . children @ 1 . children @ 0)
        DemoNode.reparent node root
        ;
    print-tree root

    drop root
    print "ok"

do
    # singleton test
    T := (Rc One)

    fn singleton ()
        using import Option
        global data : (Option T)
        if (not data)
            data = (T 17)
        'force-unwrap data

    local example : T = (copy (singleton))
    test (example == (singleton))
    ;
test ((One.refcount) == 1)
One.reset-refcount;


do
    using import struct

    # singleton test
    typedef Inner :: i32
        inline __typecall (cls v)
            bitcast v this-type

    struct T
        a : Inner

    RcT := (Rc T)

    fn singleton ()
        using import Option
        global data : (Option RcT)
        if (not data)
            data = (RcT (a = (Inner 17)))
        'force-unwrap data

    let k = (copy (singleton))
    local example : RcT = k
    # error: value of type %1000:<Rc T> must be unique
    ;

do
    using import struct

    struct T
        a = 0

    RcT := (Rc T)

    let k = (copy (RcT 17))

    local container : RcT
    dump (typeof container) (typeof k)
    container = k
    ;


# TODO: uncomment and fix
#do
    # from https://todo.sr.ht/~duangle/scopes/11

    using import Array
    using import Rc
    using import enum

    enum StorageKind
        Pointer : (Rc this-type)
        Function : (Rc (Array this-type))
        TypeReference : Symbol

        inline __copy (self)
            'apply self
                inline (T ...)
                    # uncomment next line to see corruption
                    # print ...
                    T
                        va-map
                            inline (arg)
                                copy arg
                            ...

    fn myfn (n)
        returning (uniqueof StorageKind -1)

        label done
            if (n == 0)
                merge done
                    do
                        let newST =
                            copy
                                this-function (n + 1)
                        StorageKind.Pointer (Rc.wrap newST)
            if (n == 1)
                merge done
                    do
                        local args : (Array StorageKind)
                        'append args
                            copy
                                this-function (n + 1)
                        # doesn't happen without this return.
                        return
                            StorageKind.Function (Rc.wrap (deref args))
            StorageKind.TypeReference 'unknown

    myfn 0
    print "done"
    none

;