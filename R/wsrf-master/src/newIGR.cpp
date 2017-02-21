#include "newIGR.h"

#if defined WSRF_USE_BOOST || defined WSRF_USE_C11
newIGR::newIGR(const vector<double>& gain_ratio, const vector<int>& can_var_vec,int nvars, unsigned seed)
#else
newIGR::newIGR(const vector<double>& gain_ratio, const vector<int>& can_var_vec, int nvars)
#endif
  :can_var_vec_(can_var_vec), gain_ratio_vec_(gain_ratio)
  {
    double arr[] = {0.060326388,0.256351506,0.178243939,0.561994291,0.338892245,0.540865494,0.791381954,0.060326388,0.556920311,0.526640388,0.526640388,0.526640388,0.526640388};
    typecorr = vector<double> (sizeof(arr));
    for(int i=0; i<sizeof(arr); i++){
      typecorr.push_back(arr[i]);
    }
    
    weights_ = vector<double>(can_var_vec.size()+1);
    wst_     = vector<int>(can_var_vec.size()+1);
#if defined WSRF_USE_BOOST || defined WSRF_USE_C11
    seed_    = seed;
#endif

    int n = can_var_vec.size();
    if (nvars == -1) nvars = log((double)n) / LN_2 + 1;
    nvars_ = nvars >= n ? n : nvars;
}

int newIGR::weightedSampling(int rand_num) {
    int i = 1;
    
    while (rand_num > weights_[i]) {
        rand_num -= weights_[i];
        i <<= 1;
        if (rand_num > wst_[i]) {
            rand_num -= wst_[i];
            i++;
        }
    }

    int res = i-1;
    int w = weights_[i];
    weights_[i] = 0;

    while (i != 0) {
        wst_[i] -= w;
        i >>= 1;
    }

    return res;

//    //may be the largest right is smaller RAND_MAX,because double to int may lose information
//    return n - 1;
}

/*
 * generate an integer list of size <size> according to probability
 * that is, select <size> variables by their weights
 */
vector<int> newIGR::getRandomWeightedVars() {

    //TODO: If possible, make similar RNG codes into a single function.

    int n = weights_.size()-1;
    vector<int> result(nvars_ >= n ? n : nvars_);

    if (nvars_ >= n) {
        for (int i = 0; i < n; i++)
            result[i] = i;
        return result;
    }

#if defined WSRF_USE_BOOST || defined WSRF_USE_C11
#ifdef WSRF_USE_BOOST
    boost::random::mt19937 re(seed_);
#else
    default_random_engine re {seed_};
#endif
#else
    Rcpp::RNGScope rngScope;
#endif

    for(int i = 0; i < nvars_; ++ i) {

#if defined WSRF_USE_BOOST || defined WSRF_USE_C11
#ifdef WSRF_USE_BOOST
        boost::random::uniform_int_distribution<int> uid(0, wst_[1]-1);
#else
        uniform_int_distribution<int> uid {0, wst_[1]-1};
#endif
        result[i] = weightedSampling(uid(re));
#else
        result[i] = weightedSampling(unif_rand() * wst_[1]);
#endif

    }

    return result;
}

/*
 * calculate weights of all variables according to their gain ratios
 * the results are in this->weights_
 */
void newIGR::normalizeWeight(volatile bool* pInterrupt) {

    double sum = 0;
    int n = can_var_vec_.size();
    
    for (int i = 0, j = 1; i < n; i++, j++) {

#if defined WSRF_USE_BOOST || defined WSRF_USE_C11
        if (*pInterrupt)
            return;
#else
        // check interruption
        if (check_interrupt()) throw interrupt_exception("The random forest model building is interrupted.");
#endif
        //printf("typecorr size: %d\n",sizeof(typecorr));
        //printf("can_var_vec[i]: %d\n",can_var_vec_[i]);
        //printf("typecr(c):%f\n",typecorr[8]);
        //printf("typecorr(can): %f\n", typecorr[can_var_vec_[i]]);
        weights_[j] = typecorr[can_var_vec_[i]];
        
        //printf("typecorr can_var_Vec_:%f ",typecorr[can_var_vec_[i]]);
        if(weights_[j]==0){
          weights_[j]=0.003;
        }
        sum += weights_[j];
    }
    // printf("\n");
    
    if (sum != 0) {
        for (int i = 1; i <= n; i++) {
            // printf("weight:%f \n",weights_[i]);
            weights_[i] /= sum;
            int temp = weights_[i] * RAND_MAX;
            // printf("n: %d, normalize weights: %f, count: %d\n",n, weights_[i], temp); //RAND_MAX: 32767
            
            weights_[i] = temp;
            wst_[i] = temp;
        }
    } else {
        int temp = RAND_MAX / (double) n;
        for (int i = 1; i <= n; i++) {
            weights_[i] = temp;
            wst_[i] = temp;
            printf("count: %d\n",temp);
        }
    }

    for (int i = n; i > 1; i--) {
        wst_[i>>1] += wst_[i];
        //printf("i: %d wst_: %d\n",i,wst_[i]);
    }
}

/*
 * select the most weighted variable from < this->m_ > variables that
 * are randomly picked from all varialbes according to their weights
 */

int newIGR::getSelectedIdx() {
  const vector<int>& wrs_vec = getRandomWeightedVars();
  int max = -1;
  bool is_max_set = false;
  for (int i = 0, rand_num; i < nvars_; i++) {
    rand_num = wrs_vec[i];
    if (is_max_set) {
      if (gain_ratio_vec_[rand_num] >= gain_ratio_vec_[max]) max = rand_num;
    } else {
      max = rand_num;
      is_max_set = true;
    }
  }
  // printf("getSelectedIdx: %d\n",max);
  return max;
}
// int newIGR::getSelectedIdx() {
//     const vector<int>& wrs_vec = getRandomWeightedVars();
//     int max = -1;
//     bool is_max_set = false;
//     for (int i = 0, rand_num; i < nvars_; i++) {
//         rand_num = wrs_vec[i];
//         if (is_max_set) {
//             if (weights_[can_var_vec_[rand_num]] >= weights_[can_var_vec_[max]]) max = rand_num;
//         } else {
//             max = rand_num;
//             is_max_set = true;
//         }
//     }
//     printf("getSelectedIdx: %d\n",max);
//     return max;
// }
