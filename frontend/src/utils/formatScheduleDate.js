export function formatScheduleDate(dateString) {
  const date = new Date(dateString);

  const options = {
    month: 'long',
    day: 'numeric',
    weekday: 'short', // '화'
    hour: 'numeric',
    minute: '2-digit',
    hour12: true // 12시간제 (오전/오후)
  };

  return date.toLocaleString('ko-KR', options);
}

export function formatScheduleDate2(dateString) {
  const date = new Date(dateString);

  const options = {
    hour: 'numeric',
    minute: '2-digit',
    hour12: true // 12시간제 (오전/오후)
  };

  return date.toLocaleString('ko-KR', options);
}