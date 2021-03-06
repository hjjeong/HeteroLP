\name{NEWS}
\title{News for Package 'wsrf'}

\section{Changes in wsrf version 1.5.29 (2015-10-10)}{

  \subsection{Bug Fixes}{
    \itemize{

      \item Fix out-of-bound access bug triggered by passing test data
      without the column of target variable.

    }
  }

}

\newcommand{\pkglink}{\href{http://CRAN.R-project.org/package=#1}{\pkg{#1}}}

\section{Changes in wsrf version 1.5.24 (2015-07-07)}{

  \subsection{Bug Fixes}{
    \itemize{

      \item Add missing imports to correct the NOTEs from
      \href{http://developer.r-project.org/blosxom.cgi/R-devel/NEWS/2015/06/29#n2015-06-29}{the
	newly added check item of R-devel on 2015-06-29}.

      \item Correct the URLs of CRAN package pages to their canonical
      form in the documentation.

    }
  }

  \subsection{Documentation}{
    \itemize{

      \item Add more examples in the manual.

      \item Update documentaion.

      \item Change vignette from Rnw to Rmd.

    }
  }

}

\section{Changes in wsrf version 1.5.14 (2015-06-09)}{

  \subsection{Bug Fixes}{
    \itemize{

      \item Negative information gain when splitting nodes leads to
      \code{-nan} when normalized by \code{sqrt()}.

      \item No-matched predicted results when predict type is
      "\code{prob}" (Thank \emph{Erich Studerus} for reporting this
      bug).

      \item Fix a bug when assessing variable importance: Vector out of
      range access.

    }
  }

  \subsection{Misc}{
    \itemize{

      \item Update documentation: Make it clear that the internal
      algorithm used.

      \item Move all predict functionality from \code{R} into \code{C++}
      code.

      \item Correct all compilation warnings on Windows, Linux, and OS X
      and errors from memtest.

    }
  }

}

\section{Changes in wsrf version 1.5.0 (2015-05-24)}{

  \subsection{New Features}{
    \itemize{

      \item Add new function \code{subset} to get a sub-forest from a
      \pkg{wsrf} model.
      
      \item Add new function \code{combine} to merge multiple \pkg{wsrf}
      models into bigger one.

      \item Add \code{confusion}, \code{oot.times}, \code{predicted},
      \code{useweights}, \code{mtry} in the \pkg{wsrf} model list.

      \item Add \code{clusterlogfile} in the parameter list of function
      \code{wsrf} for debug.

    }
  }

  \subsection{Changes}{
    \itemize{

      \item Function \code{predict.wsrf} returns NAs when there are
      missing values in \code{newdata}.

      \item Remove function \code{summary.wsrf}, moving its
      functionality into \code{print.wsrf}, so that the default outputs
      of \code{print.wsrf} gives a summary of the model.

      \item Tree structure printed by function \code{print.wsrf} is
      slightly different from previous version: Decisions from the same
      test node (internal node) have the same item number.

      \item Function \code{oobErrorRate} is renamed into
      \code{oob.error.rate} and made as generic.

    }
  }

  \subsection{Enhancements}{
    \itemize{

      \item Refactor code, making rooms for future modifications: Remove
      unused code; Redefine some classes.

      \item Operate on R objects directly in C++ code instead of copied
      ones.

      \item Improve time performance.

    }
  }

  \subsection{Bug Fixes}{
    \itemize{

      \item Correct weighted random sampling.

    }
  }

  \subsection{Misc}{
    \itemize{

      \item Replace \code{srand()} with \code{R} \emph{RNG} functions.

    }
  }

}

\section{Changes in wsrf version 1.4.0 (2014-05-28)}{


  \subsection{New Features}{
    \itemize{

      \item Add new function \code{wsrfParallelInfo} for querying the
      underlying parallel implementation (parallel or not).

    }
  }

  \subsection{BUG FIXES}{
    \itemize{

      \item The predict method requires a dummy outcome column to be in
      the new data (Thank \emph{Max Knhn} for reporting this bug).

    }
  }

  \subsection{Minor Changes}{
    \itemize{

      \item Adapt R's make variable \env{CXX_STD} to configure
      installation.

      \item List packages used by vignettes code in DESCRIPTION.
    }
  }

  \subsection{Documentation}{
    \itemize{

      \item Let R to build the vignettes so that it can be visible on
      \pkglink{wsrf}.

    }
  }

}


\section{Changes in wsrf version 1.3.0 (2014-02-08)}{
  \itemize{

    \item added documents:
    \itemize{
      \item vignettes
      \item README.Rd
      \item NEWS.md
    }

    \item provided Windows support (no multi-threading functionality,
    but can run in distributed way)

    \item bug fixed: when used in distributed environment, variable
    name info is missing, therefore, "Error: Training Set is empty"
    occurs.

  }
}
