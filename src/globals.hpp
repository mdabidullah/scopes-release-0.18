/*
    The Scopes Compiler Infrastructure
    This file is distributed under the MIT License.
    See LICENSE.md for details.
*/

#ifndef SCOPES_GLOBALS_HPP
#define SCOPES_GLOBALS_HPP

#include "valueref.inc"

namespace scopes {

#define SCOPES_REIMPORT_SYMBOLS() \
    T(g_none, "none") \
    T(g_sc_const_pointer_new, "sc_const_pointer_new") \
    T(g_sc_const_int_new, "sc_const_int_new") \
    T(g_sc_const_int_words_new, "sc_const_int_words_new") \
    T(g_sc_const_real_new, "sc_const_real_new") \
    T(g_sc_const_aggregate_new, "sc_const_aggregate_new") \
    T(g_deref, "deref") \
    T(g_bitcast, "bitcast") \
    T(g_voidstar, "voidstar") \
    T(g_insertvalue, "insertvalue") \
    T(g_extractvalue, "extractvalue") \
    T(g_insertelement, "insertelement") \
    T(g_extractelement, "extractelement") \
    T(g_getelementref, "getelementref") \
    T(g_store, "store") \
    T(g_load, "load") \
    T(g_move, "move") \
    T(g_view, "view") \
    T(g_lose, "lose") \
    /*T(g_copy, "copy")*/ \
    T(g_free, "free") \
    T(g_getelementptr, "getelementptr") \
    T(g_zext, "zext") \
    T(g_sext, "sext") \
    T(g_u64, "u64") \
    T(g_f64, "f64") \
    T(g_alloca, "alloca") \
    T(g_alloca_array, "alloca-array") \
    T(g_sc_scope_new, "sc_scope_new") \
    T(g_sc_scope_new_subscope, "sc_scope_new_subscope") \
    T(g_sc_scope_bind, "sc_scope_bind") \
    T(g_sc_scope_bind_with_docstring, "sc_scope_bind_with_docstring") \
    T(g_sc_scope_unbind, "sc_scope_unbind") \
    T(g_sc_eval, "sc_eval") \
    T(g_sc_eval_stage, "sc_eval_stage") \
    T(g_itrunc, "itrunc") \
    T(g_sc_const_pointer_extract, "sc_const_pointer_extract") \
    T(g_sc_const_int_extract, "sc_const_int_extract") \
    T(g_sc_const_int_extract_word, "sc_const_int_extract_word") \
    T(g_sc_const_real_extract, "sc_const_real_extract") \
    T(g_sc_const_extract_at, "sc_const_extract_at") \
    /*T(g_undef, "undef")*/ \
    T(g_fptrunc, "fptrunc") \
    T(g_fpext, "fpext") \
    T(g_sc_identity, "sc_identity") \
    T(g_sc_valueref_tag, "sc_valueref_tag") \
    T(g_sc_error_append_calltrace, "sc_error_append_calltrace") \
    T(g_sc_template_new, "sc_template_new") \
    T(g_sc_template_append_parameter, "sc_template_append_parameter") \
    T(g_sc_template_set_body, "sc_template_set_body") \
    T(g_sc_template_set_inline, "sc_template_set_inline") \
    T(g_sc_parameter_new, "sc_parameter_new") \
    T(g_sc_keyed_new, "sc_keyed_new") \
    T(g_sc_expression_new, "sc_expression_new") \
    T(g_sc_expression_append, "sc_expression_append") \
    T(g_sc_expression_set_scoped, "sc_expression_set_scoped") \
    T(g_sc_cond_new, "sc_cond_new") \
    T(g_sc_case_new, "sc_case_new") \
    T(g_sc_pass_case_new, "sc_pass_case_new") \
    T(g_sc_do_case_new, "sc_do_case_new") \
    T(g_sc_default_case_new, "sc_default_case_new") \
    T(g_sc_switch_new, "sc_switch_new") \
    T(g_sc_switch_append, "sc_switch_append") \
    T(g_sc_switch_append_case, "sc_switch_append_case") \
    T(g_sc_switch_append_pass, "sc_switch_append_pass") \
    T(g_sc_switch_append_default, "sc_switch_append_default") \
    T(g_sc_call_new, "sc_call_new") \
    T(g_sc_call_append_argument, "sc_call_append_argument") \
    T(g_sc_call_set_rawcall, "sc_call_set_rawcall") \
    T(g_sc_loop_new, "sc_loop_new") \
    T(g_sc_loop_arguments, "sc_loop_arguments") \
    T(g_sc_loop_set_body, "sc_loop_set_body") \
    T(g_sc_argument_list_new, "sc_argument_list_new") \
    T(g_sc_extract_argument_new, "sc_extract_argument_new") \
    T(g_sc_extract_argument_list_new, "sc_extract_argument_list_new") \
    T(g_sc_quote_new, "sc_quote_new") \
    T(g_sc_unquote_new, "sc_unquote_new") \
    T(g_sc_label_new, "sc_label_new") \
    T(g_sc_label_set_body, "sc_label_set_body") \
    T(g_sc_merge_new, "sc_merge_new") \


struct TypedValue;
#define T(NAME, STR) \
    extern TypedValueRef NAME;
SCOPES_REIMPORT_SYMBOLS()
#undef T

void init_globals(int argc, char *argv[]);

} // namespace scopes

#endif // SCOPES_GLOBALS_HPP