# Live Spring beans Dump

You can dump live Spring ApplicationContext to any PrintWriter or PrintStream, such as System.out or httpServletResponse.getWriter().

Just configure Dumper bean in your Spring context and call `dumper.dump(System.out)`
