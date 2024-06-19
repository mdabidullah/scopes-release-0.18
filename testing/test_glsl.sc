
using import glm
using import glsl
using import struct
using import enum

let screen-tri-vertices =
    arrayof vec2
        vec2 -1 -1; vec2  3 -1; vec2 -1  3

run-stage;

enum Location : i32
    UV
    Transform
    Phase
    Sampler

enum Binding : i32
    M

inout uv : vec2
    location = Location.UV
in transform : mat4x3
    location = Location.Transform

fn multiple-return-values ()
    if true
        _ 1 2 3 4
    else
        _ 5 6 7 8

fn set-vertex-position ()
    local screen-tri-vertices = screen-tri-vertices
    let pos = (screen-tri-vertices @ gl_VertexID)
    multiple-return-values;
    gl_Position = (vec4 (transform * (vec4 pos.x pos.y 0 1)) 1)
    deref pos

fn vertex-shader ()
    let half = (vec2 0.5 0.5)
    let pos = (set-vertex-position)
    uv.out =
        (pos * half) + half
    return;

print
    compile-glsl 0 'vertex
        typify vertex-shader
        #'O3
        #'dump-module
        #'no-opts

uniform phase : f32
    location = Location.Phase
uniform smp : sampler2D
    location = Location.Sampler
    set = 0

buffer m :
    struct MutableData plain
        value : u32
    binding = Binding.M

out out_Color : vec4
out out_UInt : u32

fn make-phase ()
    (sin phase) * 0.5 + 0.5

fn fragment-shader ()
    let uv = uv.in
    let size = (textureSize smp 0)
    let color = (vec4 uv (make-phase) size.x)
    let k j = (atomicCompSwap m.value 10 20)
    if j
        true
    else;
    # use of intrinsic
    out_UInt = (packHalf2x16 uv)
    out_Color = (color * (texture smp uv))
    return;

#'dump
    typify fragment-shader

print
    compile-glsl 0 'fragment
        typify fragment-shader
        #'dump-disassembly
        #'no-opts

shared shared-value : i32

fn compute-shader ()
    local_size 1 1 1
    shared-value = 1
    barrier;
    memoryBarrier;
    groupMemoryBarrier;
    memoryBarrierImage;
    memoryBarrierBuffer;
    memoryBarrierShared;


print
    compile-glsl 0 'compute
        typify compute-shader
        #'dump-disassembly
        #'no-opts


# TODO: fix this case
#let default-vs =
    do
        uniform mvp : mat4
        in position : vec4

        fn main ()
            (gl_Position = position * mvp)

        (compile-glsl 330 'vertex (typify main))

do
    using import struct
    using import glm
    using import glsl

    struct Uniforms plain
        mvp : mat4

    fn vert ()
        uniform u : Uniforms
            binding = 0
        gl_Position = (u.mvp * (vec4))
        ;

    print
        compile-glsl 440 'vertex (static-typify vert)
            #'dump-disassembly
        #compile-spirv 'vertex (static-typify vert)
            'dump-disassembly


# separate texture / sampler
do
    in v_tex_coords : vec2
        location = 0
    out f_color : vec4
        location = 0

    uniform t_diffuse : texture2D
        set = 0
        binding = 0
    uniform s_diffuse : sampler
        set = 0
        binding = 1

    fn main ()
        f_color =
            texture
                sampler2D t_diffuse s_diffuse
                v_tex_coords

    let bin =
        compile-spirv 0 'fragment
            typify main
            'dump-disassembly
            #'no-opts

    print bin
    print
        sc_spirv_to_glsl bin

# new instructions
do
    in v : ivec4
        location = 0
    out f : ivec4
        location = 0
    in v2 : ivec3
        location = 1
    out f2 : ivec3
        location = 1
    in v3 : f32
        location = 2
    out f3 : f32
        location = 2

    fn main ()
        f =
            typeinit
                bitreverse v.x
                bitcount v.y
                findmsb v.z
                findlsb v.w
        f2 =
            findmsb v2
        f3 =
            - v3

    print
        compile-glsl 440 'fragment
            typify main
            #'dump-disassembly
            #'no-opts

    #test ((ctpop 5:i16) == 2:i16)
    #test ((ctlz 6) == 29)
    #test ((cttz 6) == 1)
    #test ((bitreverse 0b10010110:i8) == 0b01101001:i8)

# passing pointers to shader functions
do
    using import struct
    using import glm
    using import glsl

    fn t (v)
        v

    fn vertex ()
        in x : i32
        buffer v1 :
            struct T
                member : vec4
        uniform v2 : vec4

        fn t1 () v1
        fn t2 () v2

        switch x
        case 0
            gl_Position = (t v1.member)
        case 1
            gl_Position = (t v2)
        #case 2
            gl_Position = (t1)
        case 3
            gl_Position = (t2)
        case 4
            # this works:
            gl_Position = (t (deref v1.member))
        case 5
            # or
            gl_Position = (t (deref v2))
        default
            ;

    print (compile-glsl 450 'vertex (static-typify vertex))
    none

# #15: When using GLSL version 420, spir-v generator doesn't insert GL_ARB_shader_storage_buffer_object extension
do
    fn vertex ()
        using import glsl
        using import glm
        buffer attr :
            struct Attributes plain
                data : (array vec2)
        gl_Position = (vec4 (attr.data @ gl_VertexID) 0 1)

    print
        static-compile-glsl 420 'vertex (static-typify vertex)


;
