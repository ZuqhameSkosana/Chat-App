# Makefile for Data Structures Hash Tables Assignments

SRCDIR = src
BINDIR = bin
DOCDIR = doc
JAVAC = javac
JFLAGS = -g -d $(BINDIR) -cp $(BINDIR)

vpath %.java $(SRCDIR)/
vpath %.class $(BINDIR)/

# define general build rule for java sources
.SUFFIXES:  .java  .class

.java.class:
	$(JAVAC)  $(JFLAGS)  $<

#default rule - will be invoked by make


all: MessageID.class Message.class ClientImageDisplayer.class Client.class Server.class

# The following two targets deal with the mutual dependencies:
Message.class: MessageID.class
Client.class: ClientImageDisplayer.class

# Rules for generating documentation
doc:
	javadoc -d $(DOCDIR) $(SRCDIR)/*.java

clean:
	@rm -f  $(BINDIR)/Message.class
	@rm -f  $(BINDIR)/MessageID.class
	@rm -f $(BINDIR)/ClientImageDisplayer.class
	@rm -Rf doc
