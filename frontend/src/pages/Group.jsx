import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppContext } from '../AppContext';
import axios from 'axios';
import DOMPurify from 'dompurify';
import { API_BASE_URL } from '../constants';
import { timeAgo } from '../utils/timeAgo';
import './Group.css';
import { formatScheduleDate, formatScheduleDate2 } from '../utils/formatScheduleDate';

const Group = () => {
  const { user } = useAppContext();
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
  const [newMember, setNewMember] = useState({ name: '', email: '', group: '' });
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [search, setSearch] = useState('');

  useEffect(() => {
    fetchGroupsMembers();
    fetchGroups();
  }, []);

  useEffect(() => {
    if (user === null) {
      navigate("/login");
    }
  }, [user, navigate]);

  useEffect(() => {
    if (!groups.length) return;
    groups.forEach((g) => {
      fetchGroupMembers(g.groupNo);
      fetchGroupPosts(g.groupNo);
      fetchGroupSchedules(g.groupNo);
    });
  }, [groups]);

  const fetchGroupsMembers = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/groups/members`, { withCredentials: true });
      setGroupsMembers(res.data);
    } catch (err) {
      handleError(err, "그룹 멤버 목록 불러오기 실패");
    }
  };

  const fetchGroups = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/user/groups`, { withCredentials: true });
      setGroups(res.data);
    } catch (err) {
      handleError(err, "내 그룹 불러오기 실패");
    }
  };

  const fetchGroupMembers = async (groupNo) => {
    try {
      const res = await axios.get(`${API_BASE_URL}/groups/${groupNo}/members`, { withCredentials: true });
      setGroupDetails((prev) => ({
        ...prev,
        [groupNo]: { ...(prev[groupNo] || {}), members: res.data },
      }));
    } catch (err) {
      handleError(err, "그룹 멤버 불러오기 실패");
    }
  };

  const fetchGroupPosts = async (groupNo) => {
    try {
      const res = await axios.get(`${API_BASE_URL}/groups/${groupNo}/boards`, { withCredentials: true });
      setGroupDetails((prev) => ({
        ...prev,
        [groupNo]: { ...(prev[groupNo] || {}), posts: res.data },
      }));
    } catch (err) {
      handleError(err, "그룹 게시글 불러오기 실패");
    }
  };

  const fetchGroupSchedules = async (groupNo) => {
    try {
      const res = await axios.get(`${API_BASE_URL}/groups/${groupNo}/schedules`, { withCredentials: true });
      setGroupDetails((prev) => ({
        ...prev,
        [groupNo]: { ...(prev[groupNo] || {}), schedules: res.data },
      }));
    } catch (err) {
      handleError(err, "그룹 스케줄 불러오기 실패");
    }
  };

  const filteredMembers = groupsMembers.filter((m) =>
    m.userNick.toLowerCase().includes(search.toLowerCase()) ||
    m.userEmail.toLowerCase().includes(search.toLowerCase())
  );

  const toggleSchedule = (groupNo, scheduleNo) => {
    const key = `${groupNo}-${scheduleNo}`;
    setOpenScheduleKey(prev => (prev === key ? null : key));
  };

  const handleAddGroup = async () => {
    if (!groupTitle || !groupDescription) {
      alert('그룹명, 그룹 설명을 입력해주세요.');
      return;
    }
    try {
      await axios.post(`${API_BASE_URL}/groups`, { groupTitle, groupDescription }, { withCredentials: true });
      setGroupTitle("");
      setGroupDescription("");
      setShowGroupForm(false);
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
      alert("초대 요청을 보냈습니다.");
      setNewMember({ name: '', email: '', group: '' });
    } catch (err) {
      handleError(err, "멤버 초대 실패");
    }
  };

  const handleDeleteMember = async (id) => {
    if (window.confirm('정말 탈퇴시키겠습니까?')) {
      try {
        await axios.delete(`${API_BASE_URL}/groups/${selectedGroup.groupNo}/members/${id}`, { withCredentials: true });
        fetchGroupMembers(selectedGroup.groupNo);
      } catch (err) {
        handleError(err, "멤버 강제 탈퇴 실패");
      }
    }
  };

  const handleDeleteSchedule = async (scheduleNo) => {
    if (!window.confirm("이 공유된 스케줄을 삭제하시겠습니까?")) return;
    try {
      await axios.delete(`${API_BASE_URL}/groups/${selectedGroup.groupNo}/schedules/${scheduleNo}`, { withCredentials: true });
      await fetchGroupSchedules(selectedGroup.groupNo);
    } catch (err) {
      handleError(err, "스케줄 삭제 실패");
    }
  };

  const handleAddPost = async () => {
    const title = postTitles[selectedGroup.groupNo];
    const content = postTexts[selectedGroup.groupNo];
    if (!title?.trim() || !content?.trim()) return;

    try {
      await axios.post(`${API_BASE_URL}/groups/${selectedGroup.groupNo}/boards`, {
        groupBoardTitle: title,
        groupBoardContent: content
      }, { withCredentials: true });

      await fetchGroupPosts(selectedGroup.groupNo);
      setPostTitles(prev => ({ ...prev, [selectedGroup.groupNo]: "" }));
      setPostTexts(prev => ({ ...prev, [selectedGroup.groupNo]: "" }));
      setShowPostForm(prev => ({ ...prev, [selectedGroup.groupNo]: false }));
    } catch (err) {
      handleError(err, "게시글 등록 실패");
    }
  };

  const handleDeletePost = async (groupBoardNo) => {
    if (!window.confirm("이 게시글을 삭제하시겠습니까?")) return;
    try {
      await axios.delete(`${API_BASE_URL}/groups/${selectedGroup.groupNo}/boards/${groupBoardNo}`, { withCredentials: true });
      await fetchGroupPosts(selectedGroup.groupNo);
    } catch (err) {
      handleError(err, "게시글 삭제 실패");
    }
  };

  const handleAddComment = async (groupBoardNo) => {
    const content = commentTexts[groupBoardNo];
    if (!content?.trim()) return;

    try {
      await axios.post(`${API_BASE_URL}/groups/${selectedGroup.groupNo}/boards/${groupBoardNo}/comments`, {
        groupCommentContent: content
      }, { withCredentials: true });

      await fetchGroupPosts(selectedGroup.groupNo);
      setCommentTexts((prev) => ({ ...prev, [groupBoardNo]: "" }));
    } catch (err) {
      handleError(err, "댓글 등록 실패");
    }
  };

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
      {/* Header Section */}
      <div className="group-header">
        <div className="group-header-content">
          <h1 className="group-title-main">그룹</h1>
          <p className="group-subtitle">그룹을 만들고 멤버들과 함께 소통하세요</p>
        </div>
      </div>

      <div className="group-container">
        {/* Tab Buttons */}
        <div className="group-tabs">
          <button
            className={`group-tab ${activeTab === 'member' ? 'active' : ''}`}
            onClick={() => { setActiveTab('member'); setSelectedGroup(null); }}
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
              <circle cx="9" cy="7" r="4"/>
              <path d="M23 21v-2a4 4 0 00-3-3.87"/>
              <path d="M16 3.13a4 4 0 010 7.75"/>
            </svg>
            <span>멤버 관리</span>
          </button>
          <button
            className={`group-tab ${activeTab === 'group' ? 'active' : ''}`}
            onClick={() => { setActiveTab('group'); setSelectedGroup(null); }}
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>
              <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
            <span>그룹 목록</span>
          </button>
        </div>

        {/* Member Tab Content */}
        {activeTab === 'member' && !selectedGroup && (
          <div className="tab-content">
            {/* Search & Invite Form */}
            <div className="member-controls">
              <div className="search-box">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="11" cy="11" r="8"/>
                  <path d="M21 21l-4.35-4.35"/>
                </svg>
                <input
                  type="text"
                  placeholder="이름이나 이메일 검색"
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
              </div>

              <div className="invite-form">
                <h3>멤버 초대</h3>
                <div className="form-row">
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
                    placeholder="그룹명"
                    value={newMember.group}
                    onChange={(e) => setNewMember({ ...newMember, group: e.target.value })}
                  />
                  <button className="btn-primary" onClick={handleAddMember}>초대</button>
                </div>
              </div>
            </div>

            {/* Member Table */}
            <div className="member-table-wrapper">
              <table className="member-table">
                <thead>
                  <tr>
                    <th>프로필</th>
                    <th>이름</th>
                    <th>이메일</th>
                    <th>그룹</th>
                    <th>관리</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredMembers.length === 0 ? (
                    <tr>
                      <td colSpan="5" className="empty-row">멤버가 없습니다</td>
                    </tr>
                  ) : (
                    filteredMembers.map((m, idx) => (
                      <tr key={idx}>
                        <td>
                          <img
                            src={m.userImg || "/images/default-pro.png"}
                            alt="프로필"
                            className="member-avatar"
                          />
                        </td>
                        <td>{DOMPurify.sanitize(m.userNick)}</td>
                        <td>{DOMPurify.sanitize(m.userEmail)}</td>
                        <td><span className="group-badge">{DOMPurify.sanitize(m.groupTitle)}</span></td>
                        <td>
                          {user && (m.userId !== m.groupCreatedUserId && user.userId === m.groupCreatedUserId) && (
                            <button className="btn-danger-sm" onClick={() => handleDeleteMember(m.userId)}>탈퇴</button>
                          )}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Group Tab Content */}
        {activeTab === 'group' && !selectedGroup && (
          <div className="tab-content">
            <div className="group-list-header">
              <h2>내 그룹 목록</h2>
              <button
                className={`btn-primary ${showGroupForm ? 'outline' : ''}`}
                onClick={() => setShowGroupForm(!showGroupForm)}
              >
                {showGroupForm ? '취소' : '+ 새 그룹 만들기'}
              </button>
            </div>

            {showGroupForm && (
              <div className="create-group-form">
                <div className="form-group">
                  <label>그룹명</label>
                  <input
                    type="text"
                    placeholder="그룹명을 입력하세요"
                    value={groupTitle}
                    onChange={(e) => setGroupTitle(e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label>그룹 설명</label>
                  <input
                    type="text"
                    placeholder="그룹 설명을 입력하세요"
                    value={groupDescription}
                    onChange={(e) => setGroupDescription(e.target.value)}
                  />
                </div>
                <button className="btn-primary" onClick={handleAddGroup}>그룹 생성</button>
              </div>
            )}

            <div className="group-grid">
              {groups.length === 0 ? (
                <div className="empty-state">
                  <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                    <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                    <path d="M23 21v-2a4 4 0 00-3-3.87"/>
                    <path d="M16 3.13a4 4 0 010 7.75"/>
                  </svg>
                  <p>아직 가입한 그룹이 없습니다</p>
                </div>
              ) : (
                groups.map((g, idx) => (
                  <div key={idx} className="group-card" onClick={() => setSelectedGroup(g)}>
                    <div className="group-card-icon">👥</div>
                    <div className="group-card-info">
                      <h3>{DOMPurify.sanitize(g.groupTitle)}</h3>
                      <p>{g.groupDescription ? DOMPurify.sanitize(g.groupDescription) : '설명 없음'}</p>
                    </div>
                    <svg className="group-card-arrow" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <polyline points="9 18 15 12 9 6"/>
                    </svg>
                  </div>
                ))
              )}
            </div>
          </div>
        )}

        {/* Selected Group Detail View */}
        {selectedGroup && (
          <div className="community-view">
            {/* Back Button & Title */}
            <div className="community-header">
              <button className="btn-back" onClick={() => setSelectedGroup(null)}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <polyline points="15 18 9 12 15 6"/>
                </svg>
                뒤로가기
              </button>
              <h2>{DOMPurify.sanitize(selectedGroup.groupTitle)}</h2>
            </div>

            {/* Announcement Section */}
            <div className="community-section">
              <div className="section-icon">📢</div>
              <div className="section-content">
                <h3>공지사항</h3>
                <p>{selectedGroup.groupDescription ? DOMPurify.sanitize(selectedGroup.groupDescription) : "공지사항이 없습니다."}</p>
              </div>
            </div>

            {/* Schedule Section */}
            <div className="community-section">
              <div className="section-icon">📅</div>
              <div className="section-content">
                <h3>공유된 일정</h3>
                {(groupDetails[selectedGroup.groupNo]?.schedules || []).length === 0 ? (
                  <p className="empty-text">공유된 일정이 없습니다.</p>
                ) : (
                  <div className="schedule-list">
                    {(groupDetails[selectedGroup.groupNo]?.schedules || []).map((item) => {
                      const key = `${selectedGroup.groupNo}-${item.scheduleNo}`;
                      const isOpen = openScheduleKey === key;
                      return (
                        <div key={item.scheduleNo} className={`schedule-item ${isOpen ? 'expanded' : ''}`}>
                          <div className="schedule-item-header" onClick={() => toggleSchedule(selectedGroup.groupNo, item.scheduleNo)}>
                            <div className="schedule-item-info">
                              <span className="schedule-time">
                                {formatScheduleDate(item.details[0].scheduleStartTime)} ~ {formatScheduleDate2(item.details[item.details.length - 1].scheduleEndTime)}
                              </span>
                              <span className="schedule-title">{DOMPurify.sanitize(item.scheduleName)}</span>
                            </div>
                            <div className="schedule-item-actions">
                              {user && user.userId === selectedGroup.groupCreatedUserId && (
                                <button className="btn-icon-danger" onClick={(e) => { e.stopPropagation(); handleDeleteSchedule(item.scheduleNo); }}>
                                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <polyline points="3 6 5 6 21 6"/>
                                    <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/>
                                  </svg>
                                </button>
                              )}
                              <svg className={`chevron ${isOpen ? 'open' : ''}`} width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <polyline points="6 9 12 15 18 9"/>
                              </svg>
                            </div>
                          </div>
                          {isOpen && (
                            <div className="schedule-item-details">
                              {item.details.map((d, i) => (
                                <div key={i} className="detail-row">
                                  <span className="detail-time">{formatScheduleDate2(d.scheduleStartTime)} ~ {formatScheduleDate2(d.scheduleEndTime)}</span>
                                  <span className="detail-content">{DOMPurify.sanitize(d.scheduleContent)}</span>
                                </div>
                              ))}
                            </div>
                          )}
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            </div>

            {/* Communication Section */}
            <div className="community-section posts-section">
              <div className="posts-header">
                <div className="posts-title">
                  <span className="section-icon">💬</span>
                  <h3>소통 공간</h3>
                </div>
                <button
                  className={`btn-primary ${showPostForm[selectedGroup.groupNo] ? 'outline' : ''}`}
                  onClick={() => setShowPostForm(prev => ({ ...prev, [selectedGroup.groupNo]: !prev[selectedGroup.groupNo] }))}
                >
                  {showPostForm[selectedGroup.groupNo] ? '취소' : '+ 글쓰기'}
                </button>
              </div>

              {showPostForm[selectedGroup.groupNo] && (
                <div className="post-form">
                  <input
                    type="text"
                    placeholder="제목을 입력하세요"
                    value={postTitles[selectedGroup.groupNo] || ""}
                    onChange={(e) => setPostTitles((prev) => ({ ...prev, [selectedGroup.groupNo]: e.target.value }))}
                  />
                  <textarea
                    placeholder="내용을 입력하세요"
                    value={postTexts[selectedGroup.groupNo] || ""}
                    onChange={(e) => setPostTexts((prev) => ({ ...prev, [selectedGroup.groupNo]: e.target.value }))}
                  />
                  <button className="btn-primary" onClick={handleAddPost}>등록</button>
                </div>
              )}

              <div className="posts-list">
                {(groupDetails[selectedGroup.groupNo]?.posts || []).length === 0 ? (
                  <p className="empty-text">아직 게시글이 없습니다.</p>
                ) : (
                  (groupDetails[selectedGroup.groupNo]?.posts || []).map((post) => (
                    <div key={post.groupBoardNo} className="post-item">
                      <div className="post-header">
                        <div className="post-info">
                          <h4>{DOMPurify.sanitize(post.groupBoardTitle)}</h4>
                          <span className="post-meta">{DOMPurify.sanitize(post.userNick)} · {timeAgo(post.groupBoardUpdateDate)}</span>
                        </div>
                        {user && (user.userId === selectedGroup.groupCreatedUserId || user.userId === post.userId) && (
                          <button className="btn-icon-danger" onClick={() => handleDeletePost(post.groupBoardNo)}>
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                              <polyline points="3 6 5 6 21 6"/>
                              <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/>
                            </svg>
                          </button>
                        )}
                      </div>
                      <div className="post-content">{DOMPurify.sanitize(post.groupBoardContent)}</div>

                      {/* Comments */}
                      <div className="comments-section">
                        <h5>💬 댓글 {post.comments?.length || 0}</h5>
                        {post.comments?.length > 0 && (
                          <div className="comments-list">
                            {post.comments.map((c) => (
                              <div key={c.groupCommentNo} className="comment-item">
                                <strong>{DOMPurify.sanitize(c.userNick)}</strong>
                                <span>{DOMPurify.sanitize(c.groupCommentContent)}</span>
                                <span className="comment-time">{timeAgo(c.groupCommentWriteDate)}</span>
                              </div>
                            ))}
                          </div>
                        )}
                        <div className="comment-form">
                          <input
                            type="text"
                            value={commentTexts[post.groupBoardNo] || ""}
                            placeholder="댓글을 입력하세요"
                            onChange={(e) => setCommentTexts((prev) => ({ ...prev, [post.groupBoardNo]: e.target.value }))}
                          />
                          <button className="btn-primary" onClick={() => handleAddComment(post.groupBoardNo)}>등록</button>
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>

            {/* Member List Section */}
            <div className="community-section">
              <div className="section-icon">👥</div>
              <div className="section-content">
                <h3>멤버 목록</h3>
                <div className="member-chips">
                  {(groupDetails[selectedGroup.groupNo]?.members || []).map((m) => (
                    <div key={m.userId} className="member-chip">
                      <img
                        src={m.userImg || "/images/default-pro.png"}
                        alt="프로필"
                        className="member-chip-avatar"
                      />
                      <span>{DOMPurify.sanitize(m.userNick)}</span>
                      {selectedGroup.groupCreatedUserId === m.userId && (
                        <span className="leader-badge">그룹장</span>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Group;
