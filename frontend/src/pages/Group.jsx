import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppContext } from '../AppContext'
import axios from 'axios';
import DOMPurify from 'dompurify';
import { API_BASE_URL } from '../constants';
import { timeAgo } from '../utils/timeAgo';
import './Group.css';
import { formatScheduleDate, formatScheduleDate2 } from '../utils/formatScheduleDate';

const initialMembers = [
  { id: 1, name: '김지은', email: 'jieun@example.com', group: '헬스' },
  { id: 2, name: '이철수', email: 'chulsoo@example.com', group: 'UX팀' },
  { id: 3, name: '박영희', email: 'younghee@example.com', group: 'UI' },
  { id: 4, name: '오지훈', email: 'jihun@example.com', group: '헬스' },
];

const Group = () => {
  const { user, setUser } = useAppContext();
  const [groupsMembers, setGroupsMembers] = useState([]);
  const [groups, setGroups] = useState([]);
  const [groupDetails, setGroupDetails] = useState({});
  const [showPostForm, setShowPostForm] = useState({});
  const [postTitles, setPostTitles] = useState({});
  const [postTexts, setPostTexts] = useState({});
  const [commentTexts, setCommentTexts] = useState({});
  const [openScheduleKey, setOpenScheduleKey] = useState(null);
  const [groupTitle, setGroupTitle] = useState("");
  const [groupDescription, setGroupDescription] = useState("");
  const [showGroupForm, setShowGroupForm] = useState(false);
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState('member');
  const [members, setMembers] = useState(initialMembers);
  const [newMember, setNewMember] = useState({ name: '', email: '', group: '' });
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [search, setSearch] = useState('');

  // 그룹 가져오는 api 호출
  useEffect(() => {
    fetchGroupsMembers();
    fetchGroups()
  }, []);

  useEffect(() => {
    if (user === null) {
      navigate("/login"); // 로그인 페이지로 이동
    }
  }, [user, navigate]);

  useEffect(() => {
    if (!groups.length) return;
    groups.forEach((g) => {
      fetchGroupMembers(g.groupNo);
      fetchGroupPosts(g.groupNo);
      fetchGroupSchedules(g.groupNo);
    })
  }, [groups]);

  const fetchGroupsMembers = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/groups/members`, { withCredentials: true });
      setGroupsMembers(res.data); // 서버 응답 구조에 맞게 조정
    } catch (err) {
      handleError(err, "그룹 멤버 목록 불러오기 실패");
    }
  }

  const fetchGroups = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/user/groups`, { withCredentials: true });
      setGroups(res.data); // 서버 응답 구조에 맞게 조정
    } catch (err) {
      handleError(err, "내 그룹 불러오기 실패");
    }
  }

  const fetchGroupMembers = async (groupNo) => {
    try {
      const res = await axios.get(`${API_BASE_URL}/groups/${groupNo}/members`, { withCredentials: true });
      setGroupDetails((prev) => ({
        ...prev,
        [groupNo]: {
          ...(prev[groupNo] || {}),
          members: res.data,
        },
      }));
    } catch (err) {
      handleError(err, "그룹 멤버 불러오기 실패");
    }
  }

  const fetchGroupPosts = async (groupNo) => {
    try {
      const res = await axios.get(`${API_BASE_URL}/groups/${groupNo}/boards`, { withCredentials: true });
      setGroupDetails((prev) => ({
        ...prev,
        [groupNo]: {
          ...(prev[groupNo] || {}),
          posts: res.data,
        },
      }));
    } catch (err) {
      handleError(err, "그룹 게시글 불러오기 실패");
    }
  }

  const fetchGroupSchedules = async (groupNo) => {
    try {
      const res = await axios.get(`${API_BASE_URL}/groups/${groupNo}/schedules`, { withCredentials: true });
      setGroupDetails((prev) => ({
        ...prev,
        [groupNo]: {
          ...(prev[groupNo] || {}),
          schedules: res.data,
        },
      }));
    } catch (err) {
      handleError(err, "그룹 스케줄 불러오기 실패");
    }
  }

  const filteredMembers = groupsMembers.filter((m) =>
    m.userNick.toLowerCase().includes(search.toLowerCase()) ||
    m.userEmail.toLowerCase().includes(search.toLowerCase())
  );

  const ToggleGroupForm = () => {
    setShowGroupForm(!showGroupForm);
  } 

  const toggleSchedule = (groupNo, scheduleNo) => {
    const key = `${groupNo}-${scheduleNo}`;
    setOpenScheduleKey(prev => (prev === key ? null : key));
  };

  //const groupMembers = members.filter((m) => m.group === selectedGroup);
  //const uniqueGroups = [...new Set(members.map((m) => m.group))];

  const handleAddGroup = async () => {
    if (!groupTitle || !groupDescription) {
      alert('그룹명, 그룹 설명을 입력해주세요.');
      return;
    }
    try {
      await axios.post(`${API_BASE_URL}/groups`, {
        groupTitle,
        groupDescription
      }, { withCredentials: true });
      setGroupTitle("");
      setGroupDescription("");
      fetchGroups();
    } catch (err) {
      handleError(err, "그룹 생성 실패");
    }
  };

  const handleAddMember = async () => {
    if (!newMember.name || !newMember.email || !newMember.group) {
      alert('이름, 이메일, 그룹을 입력해주세요.');
      return;
    }
    try {
      await axios.post(`${API_BASE_URL}/groups/invitations`, {
        userNick: newMember.name,
        userEmail: newMember.email,
        groupTitle: newMember.group
      }, { withCredentials: true });
      alert("초대 요청을 보냈습니다.")
    } catch (err) {
      handleError(err, "멤버 초대 실패");
    }
  };

  const handleDeleteMember = async (id) => {
    if (window.confirm('정말 탈퇴시키겠습니까?')) {
      try {
        await axios.delete(`${API_BASE_URL}/groups/${selectedGroup.groupNo}/members/${id}`, { withCredentials: true });
        setMembers(members.filter((m) => m.userId !== id));
      } catch (err) {
        handleError(err, "멤버 강제 탈퇴 실패");
      }
      
    }
  };

  const handleDeleteSchedule = async (scheduleNo) => {
    if (!window.confirm("이 공유된 스케줄을 삭제하시겠습니까?")) return;

    try {
      await axios.delete(`${API_BASE_URL}/groups/${selectedGroup.groupNo}/schedules/${scheduleNo}`, { withCredentials: true });

      // 삭제 후 스케줄 목록 새로고침
      await fetchGroupSchedules(selectedGroup.groupNo);
    } catch (err) {
      handleError(err, "스케줄 삭제 실패");
    }
  }

  const handleAddPost = async () => {
    const title = postTitles[selectedGroup.groupNo];
    const content = postTexts[selectedGroup.groupNo];
    if (!title?.trim() || !content?.trim()) return;

    try {
      await axios.post(`${API_BASE_URL}/groups/${selectedGroup.groupNo}/boards`, {
        groupBoardTitle: title,
        groupBoardContent: content
      }, { withCredentials: true });

      await fetchGroupPosts(selectedGroup.groupNo); // 게시글 + 댓글 다시 불러오기
      // 입력창 초기화 + 폼 숨기기
      setPostTitles(prev => ({ ...prev, [selectedGroup.groupNo]: "" }));
      setPostTexts(prev => ({ ...prev, [selectedGroup.groupNo]: "" }));
      setShowPostForm(prev => ({ ...prev, [selectedGroup.groupNo]: false }));
    } catch (err) {
      handleError(err, "게시글 등록 실패");
    }
  }

  const handleDeletePost = async (groupBoardNo) => {
    if (!window.confirm("이 게시글을 삭제하시겠습니까?")) return;

    try {
      await axios.delete(`${API_BASE_URL}/groups/${selectedGroup.groupNo}/boards/${groupBoardNo}`, { withCredentials: true });

      // 삭제 후 게시글 목록 새로고침
      await fetchGroupPosts(selectedGroup.groupNo);
    } catch (err) {
      handleError(err, "게시글 삭제 실패");
    }
  }

  const handleAddComment = async (groupBoardNo) => {
    const content = commentTexts[groupBoardNo];
    if (!content?.trim()) return;

    try {
      await axios.post(`${API_BASE_URL}/groups/${selectedGroup.groupNo}/boards/${groupBoardNo}/comments`, {
        groupCommentContent: content
      }, { withCredentials: true });

      await fetchGroupPosts(selectedGroup.groupNo); // 게시글 + 댓글 다시 불러오기

      // 입력창 초기화
      setCommentTexts((prev) => ({
        ...prev,
        [groupBoardNo]: "",
      }));
    } catch (err) {
      handleError(err, "댓글 등록 실패");
    }
  }

  const handleError = (err, defaultMessage) => {
    if (err.response?.data?.message) {
      if (err.response.data.message === "로그인이 필요합니다.") {
        navigate("/login");
      } else {
        alert(err.response.data.message);
      }
    } else {
      console.error(`${defaultMessage}:`, err);
    }
  };

  return (
    <div className="group-page">
      <div className="menu-offset" />

      <div className="group-tab-buttons">
        <button
          className={activeTab === 'member' ? 'active-tab' : ''}
          onClick={() => { setActiveTab('member'); setSelectedGroup(null); }}
        >
          Member
        </button>
        <button
          className={activeTab === 'group' ? 'active-tab' : ''}
          onClick={() => { setActiveTab('group'); setSelectedGroup(null); }}
        >
          Group
        </button>
      </div>

      <div className="group-main-content">
        {activeTab === 'member' && !selectedGroup && (
          <>
            <div className="search-add-wrapper">
              <input
                type="text"
                placeholder="이름이나 이메일 검색"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
              <div className="add-member-form">
                <input
                  type="text"
                  placeholder="이름"
                  value={newMember.name}
                  onChange={(e) => setNewMember({ ...newMember, name: e.target.value })}
                />
                <input
                  type="email"
                  placeholder="이메일"
                  value={newMember.email}
                  onChange={(e) => setNewMember({ ...newMember, email: e.target.value })}
                />
                <input
                  type="text"
                  placeholder="그룹"
                  value={newMember.group}
                  onChange={(e) => setNewMember({ ...newMember, group: e.target.value })}
                />
                <button onClick={handleAddMember}>멤버 초대</button>
              </div>
            </div>

            <table className="member-table">
              <thead>
                <tr>
                  <th>프로필 사진</th>
                  <th>이름</th>
                  <th>이메일</th>
                  <th>그룹</th>
                  <th>비고</th>
                </tr>
              </thead>
              <tbody>
                {filteredMembers.map((m, idx) => (
                  <tr key={idx}>
                    <td><img src={m.userImg || "/images/default-pro.png"} alt="프로필" className="group-member-avatar" /></td>
                    <td>{DOMPurify.sanitize(m.userNick)}</td>
                    <td>{DOMPurify.sanitize(m.userEmail)}</td>
                    <td>{DOMPurify.sanitize(m.groupTitle)}</td>
                    <td>
                      {user && (m.userId !== m.groupCreatedUserId && user.userId === m.groupCreatedUserId) &&
                      <button onClick={() => handleDeleteMember(m.userId)}>강제 탈퇴</button>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </>
        )}

        {activeTab === 'group' && !selectedGroup && (
          <div className="group-list">
            <h3>그룹 목록</h3>
            <button onClick={() => {
                ToggleGroupForm();
              }}>
                {showGroupForm ? '작성 취소' : '그룹 생성'}
              </button>
              {showGroupForm && (
                <div className="group-form">
                  <input
                    type="text"
                    className="group-title-input"
                    placeholder="그룹명을 입력하세요..."
                    value={groupTitle}
                    onChange={(e) =>
                      setGroupTitle(e.target.value)
                    }
                  />
                  <input
                    type="text"
                    className="group-description"
                    placeholder="그룹 설명을 입력하세요..."
                    value={groupDescription}
                    onChange={(e) =>
                      setGroupDescription(e.target.value)
                    }
                  />
                  <button className="group-submit" onClick={handleAddGroup}>
                    등록
                  </button>
                </div>
              )}
            <ul>
              {groups.map((g, idx) => (
                <li key={idx} onClick={() => setSelectedGroup(g)} style={{ cursor: 'pointer' }}>
                  ✅ {DOMPurify.sanitize(g.groupTitle)} 커뮤니티
                </li>
              ))}
            </ul>
          </div>
        )}

        {selectedGroup && (
          <div className="community-page">
            <h2>{DOMPurify.sanitize(selectedGroup.groupTitle)} 커뮤니티</h2>
            <button onClick={() => setSelectedGroup(null)}>← 뒤로가기</button>

            <div className="community-section">
              <h3>공지사항</h3>
              <p>{selectedGroup.groupDescription ? DOMPurify.sanitize(selectedGroup.groupDescription) : "공지사항이 없습니다."}</p>
            </div>

            <div className="community-section">
              <h3>스케줄</h3>
              <p>{(groupDetails[selectedGroup.groupNo]?.schedules || []).map((item) => {
                const key = `${selectedGroup.groupNo}-${item.scheduleNo}`;
                const isOpen = openScheduleKey === key;
                return (
                <li key={item.scheduleNo} className="schedule-item" onClick={() => toggleSchedule(selectedGroup.groupNo, item.scheduleNo)}>
                  <div>
                    <strong>{formatScheduleDate(item.details[0].scheduleStartTime)}</strong> ~
                    <strong> {formatScheduleDate2(item.details[item.details.length - 1].scheduleEndTime)}</strong>
                  </div>
                  <div>{DOMPurify.sanitize(item.scheduleName)}</div>
                  {user && user.userId === selectedGroup.groupCreatedUserId
                  ? <button onClick={() => handleDeleteSchedule(item.scheduleNo)}>삭제</button>
                  : <div></div>}
                  {isOpen && (
                    <>
                      <div className="schedule-details">
                        <h3>세부 내용</h3>
                        {item.details.map((d) => (
                          <div style={{"padding-bottom": "10px"}}>
                            <div>
                              <strong>{formatScheduleDate2(d.scheduleStartTime)}</strong> ~
                              <strong> {formatScheduleDate2(d.scheduleEndTime)}</strong>
                            </div>
                            <div>{DOMPurify.sanitize(d.scheduleContent)}</div>
                          </div>
                      ))}
                      </div>
                    </>
                  )}
                </li>
              )})}</p>
            </div>

            <div className="community-section">
              <h3>소통 공간</h3>
              <p>그룹 멤버들과 자유롭게 이야기 나눠보세요!</p>
              <button onClick={() => {
                const groupNo = selectedGroup.groupNo;
                setShowPostForm(prev => ({
                  ...prev,
                  [groupNo]: !prev[groupNo],
                }));
              }}>
                {showPostForm[selectedGroup.groupNo] ? '작성 취소' : '게시글 등록'}
              </button>

              {showPostForm[selectedGroup.groupNo] && (
                <div className="post-form">
                  <input
                    type="text"
                    className="post-title-input"
                    placeholder="제목을 입력하세요..."
                    value={postTitles[selectedGroup.groupNo] || ""}
                    onChange={(e) =>
                      setPostTitles((prev) => ({
                        ...prev,
                        [selectedGroup.groupNo]: e.target.value,
                      }))
                    }
                  />
                  <textarea
                    className="post-textarea"
                    placeholder="내용을 입력하세요..."
                    value={postTexts[selectedGroup.groupNo] || ""}
                    onChange={(e) =>
                      setPostTexts((prev) => ({
                        ...prev,
                        [selectedGroup.groupNo]: e.target.value,
                      }))
                    }
                  />
                  <button className="post-submit" onClick={handleAddPost}>
                    등록
                  </button>
                </div>
              )}

              <div className="post-list">
                {(groupDetails[selectedGroup.groupNo]?.posts || []).map((post) => (
                  <div key={post.groupBoardNo} className="post-item" style={{display: "block"}}>
                    <h2>{DOMPurify.sanitize(post.groupBoardTitle)}</h2>
                    <p className="timestamp">{DOMPurify.sanitize(post.userNick)} · {timeAgo(post.groupBoardUpdateDate)}</p>
                    {user && (user.userId === selectedGroup.groupCreatedUserId || user.userId === post.userId) &&
                    <button className="post-delete" onClick={() => handleDeletePost(post.groupBoardNo)}>삭제</button>}
                    <div className="content">{DOMPurify.sanitize(post.groupBoardContent)}</div>
                    <div className="comment-section">
                      <h4 style={{margin: "0"}}>💬 댓글</h4>
                      {post.comments?.length > 0 ? (
                        <ul className="comment-list">
                          {post.comments.map((c) => (
                            <li key={c.groupCommentNo} className="comment-item">
                              <strong>{DOMPurify.sanitize(c.userNick)}</strong>: {DOMPurify.sanitize(c.groupCommentContent)} <span className="timestamp">{timeAgo(c.groupCommentWriteDate)}</span>
                            </li>
                          ))}
                        </ul>
                      ) : (
                        <p className="no-comment">아직 댓글이 없습니다.</p>
                      )}
                      <div className="comment-form">
                        <input
                          type="text"
                          value={commentTexts[post.groupBoardNo] || ""}
                          placeholder="댓글을 입력하세요..."
                          className="comment-input"
                          onChange={(e) => setCommentTexts((prev) => ({
                            ...prev,
                            [post.groupBoardNo]: e.target.value,
                          }))}
                        />
                        <button className="comment-submit" onClick={() => handleAddComment(post.groupBoardNo)}>등록</button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="community-section group-member-list">
              <h3>멤버 목록</h3>
              <ul className="group-member-items">
                {(groupDetails[selectedGroup.groupNo]?.members || []).map((m) => (
                  <li key={m.userId} className="group-member-item">
                    <img
                      src={m.userImg || "/images/default-pro.png"}
                      alt="프로필"
                      className="group-member-avatar"
                    />
                    <span className="group-member-nick">
                      {DOMPurify.sanitize(m.userNick)}
                      {selectedGroup.groupCreatedUserId === m.userId && (
                        <span className="group-member-badge">그룹장</span>
                      )}
                    </span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Group;
