import { useState, useRef, useCallback } from 'react';
import axios from 'axios';
import { API_BASE_URL } from '../constants.js';

// 캐시 저장소 (모듈 레벨에서 공유)
const cache = new Map();
const CACHE_EXPIRY_MS = 5 * 60 * 1000; // 5분

/**
 * 자동완성 훅 - 캐싱 및 디바운싱 지원
 * @param {number} debounceMs - 디바운스 딜레이 (기본 300ms)
 * @param {number} maxResults - 최대 결과 수 (기본 10)
 * @returns {object} { suggestions, fetchSuggestions, clearSuggestions }
 */
const useAutocomplete = (debounceMs = 300, maxResults = 10) => {
  const [suggestions, setSuggestions] = useState({});
  const timeoutRef = useRef(null);
  const abortControllerRef = useRef(null);

  // 캐시에서 결과 가져오기
  const getCachedResult = (query) => {
    const cached = cache.get(query.toLowerCase());
    if (cached && Date.now() - cached.timestamp < CACHE_EXPIRY_MS) {
      return cached.data;
    }
    // 만료된 캐시 삭제
    if (cached) {
      cache.delete(query.toLowerCase());
    }
    return null;
  };

  // 캐시에 결과 저장
  const setCachedResult = (query, data) => {
    cache.set(query.toLowerCase(), {
      data,
      timestamp: Date.now()
    });

    // 캐시 크기 제한 (최대 100개)
    if (cache.size > 100) {
      const firstKey = cache.keys().next().value;
      cache.delete(firstKey);
    }
  };

  // 자동완성 요청
  const fetchSuggestions = useCallback(async (query, key = 'default') => {
    // 이전 요청 취소
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }

    // 빈 쿼리 처리
    if (!query || !query.trim()) {
      setSuggestions(prev => ({ ...prev, [key]: [] }));
      return;
    }

    const trimmedQuery = query.trim();

    // 캐시 확인
    const cachedData = getCachedResult(trimmedQuery);
    if (cachedData) {
      setSuggestions(prev => ({ ...prev, [key]: cachedData }));
      return;
    }

    // 새 요청 생성
    abortControllerRef.current = new AbortController();

    try {
      const res = await axios.get(
        `${API_BASE_URL}/map/autocomplete?name=${encodeURIComponent(trimmedQuery)}`,
        {
          signal: abortControllerRef.current.signal,
          timeout: 5000
        }
      );

      const data = res.data.slice(0, maxResults);

      // 캐시에 저장
      setCachedResult(trimmedQuery, data);

      setSuggestions(prev => ({ ...prev, [key]: data }));
    } catch (err) {
      if (axios.isCancel(err)) {
        // 요청이 취소된 경우 무시
        return;
      }
      console.error('자동완성 오류:', err);
      setSuggestions(prev => ({ ...prev, [key]: [] }));
    }
  }, [maxResults]);

  // 디바운스된 자동완성 요청
  const debouncedFetch = useCallback((query, key = 'default') => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }
    timeoutRef.current = setTimeout(() => {
      fetchSuggestions(query, key);
    }, debounceMs);
  }, [fetchSuggestions, debounceMs]);

  // 특정 키의 제안 초기화
  const clearSuggestions = useCallback((key = 'default') => {
    setSuggestions(prev => ({ ...prev, [key]: [] }));
  }, []);

  // 모든 제안 초기화
  const clearAllSuggestions = useCallback(() => {
    setSuggestions({});
  }, []);

  // 캐시 통계 (디버깅용)
  const getCacheStats = useCallback(() => {
    return {
      size: cache.size,
      keys: Array.from(cache.keys())
    };
  }, []);

  return {
    suggestions,
    fetchSuggestions: debouncedFetch,
    fetchSuggestionsImmediate: fetchSuggestions,
    clearSuggestions,
    clearAllSuggestions,
    getCacheStats
  };
};

// 캐시 초기화 함수 (외부에서 호출 가능)
export const clearAutocompleteCache = () => {
  cache.clear();
};

export default useAutocomplete;
