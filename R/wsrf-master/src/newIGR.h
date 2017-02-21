#ifndef newIGR_H_
#define newIGR_H_

#if defined WSRF_USE_BOOST || defined WSRF_USE_C11
#ifdef WSRF_USE_BOOST
#include <boost/random/mersenne_twister.hpp>
#include <boost/random/uniform_int_distribution.hpp>
#else
#include<random>
#endif
#endif

#include "utility.h"

using namespace std;

class newIGR {
private:
    int nvars_;  //subspace size
#if defined WSRF_USE_BOOST || defined WSRF_USE_C11
    unsigned seed_;
#endif
    const vector<int>& can_var_vec_;
    vector<double> weights_;
    vector<int>    wst_;
    vector<double>   typecorr;
    const vector<double>& gain_ratio_vec_;
    
    int weightedSampling(int random_integer);
    inline vector<int> getRandomWeightedVars();
public:
#if defined WSRF_USE_BOOST || defined WSRF_USE_C11
    newIGR(const vector<double>& gain_ratio, const vector<int>& can_var_vec, int nvars, unsigned seed);
#else
    newIGR(const vector<double>& gain_ratio, const vector<int>& can_var_vec, int nvars);
#endif

    void normalizeWeight(volatile bool* pInterrupt);
    int  getSelectedIdx();
};

#endif /* IGR_H_ */
