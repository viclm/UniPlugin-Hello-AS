/**
 * 获取文件,文件不存在会自动创建()
 * @param {Object} fileName 可以是文件名，也可以是/路径/文件名
 * @param {Object} dirEntry 文件对象,可以为空
 * @return 文件Entry
 */
export async function getFileEntryAsync(fileName, dirEntry) {
  // console.log("[getFileEntryAsync]开始执行")
  return new Promise((resolve) => {
    plus.io.requestFileSystem(plus.io.PUBLIC_DOCUMENTS, function (fs) {
      // console.log("[getFileEntryAsync]fileName is :" + fileName)
      let entry = dirEntry || fs.root;
      entry.getFile(
        fileName,
        { create: true },
        function (fileEntry) {
          // console.log("[getFileEntryAsync] 执行完成")
          resolve(fileEntry);
        },
        function (ex) {
          console.log(ex);
        }
      );
    });
  });
}

/**
 * 获取文件夹，不存在会自动创建
 * @param {Object} dirName
 */
export async function getDirEntryAsync(dirName) {
  return new Promise(async (resolve) => {
    plus.io.requestFileSystem(plus.io.PUBLIC_DOCUMENTS, function (fs) {
      fs.root.getDirectory(
        dirName,
        {
          create: true,
        },
        function (dirEntry) {
          resolve(dirEntry);
        }
      );
    });
  });
}

/**
 * 获取通过fileEntry获取file，不存在会自动创建
 * @param {Object} fileName
 * @param {Object} dirEntry
 */
export async function getFileAsync(fileName, dirEntry) {
  // console.log("[getFileAsync]")
  return new Promise(async (resolve) => {
    let fileEntry = await getFileEntryAsync(fileName, dirEntry);
    fileEntry.file(function (file) {
      resolve(file);
    });
  });
}

/**
 * 读取文件中的内容
 * @param {Object} path
 * @param {Object} dirEntry
 */
export async function getFileContextAsync(path, dirEntry) {
  let deffered;
  let fileReader = new plus.io.FileReader();
  fileReader.onloadend = function (evt) {
    deffered(evt.target);
  };
  let file = await getFileAsync(path, dirEntry);
  fileReader.readAsText(file, 'utf-8');
  return new Promise((resolve) => {
    deffered = resolve;
  });
}

/**
 * 向文件中写入数据
 * @param {Object} path 要写入数据的文件的位置
 * @param {Object} fileContext 要写入的内容
 * @param {Object} dirEntry 文件夹，可不写使用默认
 */
export async function writeContextToFileAsync(path, fileContext, dirEntry) {
  let fileEntry = await getFileEntryAsync(path, dirEntry);
  let file = await getFileAsync(path, dirEntry);
  return new Promise((resolve) => {
    fileEntry.createWriter(async (writer) => {
      // 写入文件成功完成的回调函数
      writer.onwrite = (e) => {
        resolve();
      };
      // 写入数据
      writer.write(fileContext);
    });
  });
}

/**
 * 追加写入数据
 * @param {Object} path 要写入数据的文件的位置
 * @param {Object} fileContext 要写入的内容
 * @param {Object} dirEntry 文件夹，可不写使用默认
 */
export async function AddOnWrite(path, fileContext, dirEntry) {
  let data = await getFileContextAsync(path, dirEntry);
  let fileEntry = await getFileEntryAsync(path, dirEntry);
  let file = await getFileAsync(path, dirEntry);
  return new Promise((resolve) => {
    fileEntry.createWriter(async (writer) => {
      // 写入文件成功完成的回调函数
      writer.onwrite = (e) => {
        resolve(true);
      };
      // 写入数据
      writer.write(data.result + fileContext);
    });
  });
}

/**
 * 判断文件是否存在
 * @param {Object} fileName
 * @param {Object} dirEntry
 */
export async function existFileAsync(fileName, dirEntry) {
  return new Promise((resolve) => {
    plus.io.requestFileSystem(plus.io.PUBLIC_DOCUMENTS, function (fs) {
      let entry = dirEntry || fs.root;
      let directoryReader = entry.createReader();
      directoryReader.readEntries(function (entries) {
        let isExist = entries.some((entry) => entry.name === fileName);
        resolve(isExist);
      });
    });
  });
}

/**
 * 遍历dirEntry，只遍历当前目录,深层次的目录暂不考虑
 * @param {Object} dirEntry 目录名,若是为空则为应用沙盒目录
 */
export async function iterateDirectory(dirEntry) {
  if (dirEntry) {
    var dir = await getDirEntryAsync(dirEntry);
  }
  return new Promise((resolve) => {
    plus.io.requestFileSystem(plus.io.PUBLIC_DOCUMENTS, function (fs) {
      let entry = dir || fs.root;
      let directoryReader = entry.createReader();
      directoryReader.readEntries(
        function (entries) {
          // entries.forEach((item, idx, arr)=>{
          // 	if(idx===0) console.log("---------------"+entry.name+"目录-----------------");
          // 	console.log(idx+1, item.name);
          // 	if(idx===arr.length-1) console.log("---------------end-----------------");
          // })
          resolve(entries);
        },
        function (e) {
          console.log('Read entries failed: ' + e.message);
        }
      );
    });
  });
}
