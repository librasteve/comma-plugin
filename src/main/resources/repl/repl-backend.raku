# Script used to back the Comma REPL support. It does largely what
# the real REPL does: build up a set of nested closures. It receives
# commands to execute, runs them, and produces the output. The commands
# are in an envelope of the form "EVAL <number-of-lines>\n" so we can
# handle multi-line inputs easily.
#
# Control messages in the output are sent back as a line with a control
# char \x01 at the start. These are used to wrap certain kinds of more
# interesting output, such as errors. They are:
#    \x01 COMPILE-ERROR-START
#    \x01 RUNTIME-ERROR-START
#    \x01 AWAIT-BACKTRACE-END
#    \x01 ERROR-END
#    \x01 COMPILED-OK
#    \x01 ERROR-SPLIT

use MONKEY;

# Disable any output buffering.
$*OUT.out-buffer = 0;
$*ERR.out-buffer = 0;

my $*NEXT-EVAL = -> $code { EVAL $code };
loop {
    my $command = $*IN.get;
    given $command {
        my $/;
        when /^'EVAL '(\d+)$/ {
            my @lines = (^+$0).map({ $*IN.get });
            my $code = '{ use MONKEY; $*NEXT-EVAL = -> $code { EVAL $code }; note "\x01 COMPILED-OK"; ' ~
                "\n" ~ @lines.join("\n") ~ "\n}";
            my $output-pos = $*OUT.tell;
            my $result = $*NEXT-EVAL($code);
            if $*OUT.tell == $output-pos {
                say $result;
            }
            CATCH {
                when X::Comp {
                    report-compile-errors([$_]);
                }
                when X::Comp::Group {
                    report-compile-errors(flat .sorrows, .panic);
                }
                when X::REPL::InvalidEnvironment {
                    note "Cannot run repl() from within Comma\'s internal REPL as it is not a TTY";
                }
                default {
                    note "\x01 RUNTIME-ERROR-START";
                    for .*await-backtrace {
                        note tidy-backtrace($_);
                        note "\x01 AWAIT-BACKTRACE-END";
                    }
                    note tidy-backtrace(.backtrace);
                    note "---";
                    note .message;
                    note "\x01 ERROR-END";
                }
            }
        }
    }
}

sub report-compile-errors(@errors) {
    note "\x01 COMPILE-ERROR-START";
    note @errors.elems;
    for @errors {
        note .line - 1;
        note .pre // '';
        note .post // '';
        note .message;
        note "\x01 ERROR-SPLIT";
    }
    note "\x01 ERROR-END";
}

sub tidy-backtrace($backtrace) {
    $backtrace.nice.split("\n")
          .grep(* !~~ /"at $*PROGRAM.absolute()"/)
          .map(*.subst(/'at EVAL_'\d+ ' line ' (\d+)/, -> $/ { "at REPL evaluation line {$0 - 1}" }))
          .join("\n")
}
