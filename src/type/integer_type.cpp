/*
    The Scopes Compiler Infrastructure
    This file is distributed under the MIT License.
    See LICENSE.md for details.
*/

#include "integer_type.hpp"
#include "../utils.hpp"
#include "../dyn_cast.inc"

namespace scopes {

#if defined(__aarch64__)
#define SCOPES_INTEGER_WORDSIZE 16
#else
#define SCOPES_INTEGER_WORDSIZE 8
#endif

//------------------------------------------------------------------------------
// INTEGER TYPE
//------------------------------------------------------------------------------

static bool is_standard_integer_size(int width) {
    switch (width) {
    case 8:
    case 16:
    case 32:
    case 64:
        return true;
    default: return false;
    }
}

void IntegerType::stream_name(StyledStream &ss) const {
    if ((width == 1) && !issigned) {
        ss << "bool";
    } else if (is_standard_integer_size(width)) {
        if (issigned) {
            ss << "i";
        } else {
            ss << "u";
        }
        ss << width;
    } else {
        ss << "(";
        if (issigned) {
            ss << "signed ";
        }
        ss << "integer " << width;
        ss << ")";
    }
}

IntegerType::IntegerType(size_t _width, bool _issigned)
    : Type(TK_Integer), width(_width), issigned(_issigned) {
    auto bytes = (width + 7) / 8;
    if (bytes <= SCOPES_INTEGER_WORDSIZE) {
        size = ceilpow2(bytes);
    } else {
        size = scopes::align(bytes, SCOPES_INTEGER_WORDSIZE);
    }
    align = std::min(size, size_t(SCOPES_INTEGER_WORDSIZE));
}

static const Type *_Integer(size_t _width, bool _issigned) {
    return new IntegerType(_width, _issigned);
}
static auto m_Integer = memoize(_Integer);

const Type *integer_type(size_t _width, bool _issigned) {
    return m_Integer(_width, _issigned);
}

int integer_type_bit_size(const Type *T) {
    return (int)cast<IntegerType>(T)->width;
}

} // namespace scopes

