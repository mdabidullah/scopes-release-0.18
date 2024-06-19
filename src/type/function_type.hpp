/*
    The Scopes Compiler Infrastructure
    This file is distributed under the MIT License.
    See LICENSE.md for details.
*/

#ifndef SCOPES_FUNCTION_HPP
#define SCOPES_FUNCTION_HPP

#include "../type.hpp"
#include "../result.hpp"

namespace scopes {

//------------------------------------------------------------------------------
// FUNCTION TYPE
//------------------------------------------------------------------------------

enum {
    // takes variable number of arguments
    FF_Variadic = (1 << 0),
};

struct FunctionType : Type {
    static bool classof(const Type *T);

    FunctionType(
        const Type *_except_type,
        const Type *_return_type,
        const Types &_argument_types, uint32_t _flags);

    void stream_name(StyledStream &ss) const;

    bool vararg() const;
    bool has_exception() const;
    bool returns_value() const;

    const FunctionType *strip_annotations() const;

    SCOPES_RESULT(const Type *) type_at_index(size_t i) const;

    const Type *except_type;
    const Type *return_type;
    Types argument_types;
    uint32_t flags;
    mutable const FunctionType *stripped;
};

void canonicalize_argument_types(Types &types);

const Type *raising_function_type(const Type *except_type, const Type *return_type,
    Types argument_types, uint32_t flags = 0);
const Type *raising_function_type(const Type *return_type,
    const Types &argument_types, uint32_t flags = 0);
const Type *function_type(const Type *return_type,
    const Types &argument_types, uint32_t flags = 0);

bool is_function_pointer(const Type *type);

const FunctionType *extract_function_type(const Type *T);

SCOPES_RESULT(void) verify_function_pointer(const Type *type);

} // namespace scopes

#endif // SCOPES_FUNCTION_HPP