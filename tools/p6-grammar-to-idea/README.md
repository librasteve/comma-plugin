# Perl 6 grammar-ish to IDEA platform Lexer/Parser

This is a tool that takes something that looks very much like a Perl 6 grammar
and produces Java code implementing the IDEA platform `Lexer` and `PsiParser`
interfaces.

This turns out to be a hard problem, since Perl 6 grammars, used to specify
the Perl 6 language:

* Rely on being able to nest sub-languages as deep as needed inside of each
  other (`my $rx = /"$foo."bar"()"+/;` has MAIN, Regex, Quote, MAIN, Quote
  at its deepest point).
* Don't really distinguish lexing and parsing (it "writes a lexer for you",
  but some things are handled imperatively rather than by those lexers). By
  contrast, IntelliJ strongly decouples the lexing and parsing phases, and
  the current lexer state is only recorded as an integer.

Therefore, the integer used to represent the current state consists of two
16-bit values:

* The upper 16 bits represent a braid stack, and index to resolve it
* The lower 16 bits represent a parse state within that braid

## The grammar language

The grammar language is a restricted, and in some places slightly extended,
version of the Perl 6 grammar syntax. The input file should consist of one or
more grammars, with one of them representing the main language being called
`MAIN`. Inside each `grammar`, `rule` and `token` are supported, with `rule`
doing the automatic insertion of `<.ws>` calls.

The following constructs inside of those behave as in Perl 6 grammars:

* Single line comments with `#`
* `||` for sequential alternation
* `|` for LTM alternation
* `[...]` grouping
* `<foo>` and `<.foo>`
* The `*`, `+`, and `?` quantifiers 
* The `$` anchor
* Single quotes (`'like this'`) for literals
* `<?foo>`, `<!foo>`, `<?before ...>`, `<!before ...>`
* Basic character classes, without ranges, common backslash sequences, but
  only a single element and no negation
* Escape character classes: `\d`, `\w`, `\h`, `\s`, `\r`, `\n`, `\v` and
  their uppercase negative forms
* Escapes of non-word characters (like `\\`)
* Proto tokens, together with the special rule `<sym>`
* `>>`, `«'`, `<<`, and `»` word boundaries

Todo:

* $<capture>=...
* ~ goal matching syntax
* Character class ranges
* The $$ anchor
* % for separators
* Some MARKER equivalent
* Some way to report syntax errors, but the continue
* Builtin rules: ident, sym
* A way to call into another language