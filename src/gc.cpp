/*
    The Scopes Compiler Infrastructure
    This file is distributed under the MIT License.
    See LICENSE.md for details.
*/

#include "gc.hpp"
#include "error.hpp"
#include "scopes/config.h"

#include <algorithm>
#include <map>

namespace scopes {

// for allocated pointers, register the size of the range
static std::map<void *, size_t> tracked_allocations;

void track(void *ptr, size_t size) {
    tracked_allocations.insert({ptr,size});
}

void *tracked_malloc(size_t size) {
    void *ptr = malloc(size);
    track(ptr, size);
    return ptr;
}

bool find_allocation(void *srcptr,  void *&start, size_t &size) {
    auto it = tracked_allocations.upper_bound(srcptr);
    if (it == tracked_allocations.begin())
        return false;
    it--;
    start = it->first;
    size = it->second;
    return (srcptr >= start)&&((uint8_t*)srcptr < ((uint8_t*)start + size));
}

} // namespace scopes